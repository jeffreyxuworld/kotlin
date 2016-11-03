/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.backend.common.SerializerCodegen
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.codegen.context.MethodContext
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.diagnostics.OtherOriginFir
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.kotlin.serialization.deserialization.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes.*
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class SerializerCodegenImpl(
        private val codegen: ImplementationBodyCodegen
) : SerializerCodegen(codegen.myClass, codegen.bindingContext) {
    private val internalPackageFqName = FqName("kotlin.internal")
    private val descType = Type.getObjectType("kotlin/serialization/KSerialClassDesc")
    private val descImplType = Type.getObjectType("kotlin/internal/SerialClassDescImpl")
    private val kSerializerType = Type.getObjectType("kotlin/serialization/KSerializer")
    private val kSerialSaverType = Type.getObjectType("kotlin/serialization/KSerialSaver")
    private val kSerialLoaderType = Type.getObjectType("kotlin/serialization/KSerialLoader")
    private val serialDescField = "\$\$serialDesc"

    private val enumSerializerId = ClassId(internalPackageFqName, Name.identifier("EnumSerializer"))

    private val classAsmType = codegen.typeMapper.mapClass(codegen.descriptor)

    companion object {
        @JvmStatic
        fun generateSerializerExtensions(codegen: ImplementationBodyCodegen) {
            if (!isSerializerCompanion(codegen.descriptor)) return
            SerializerCodegenImpl(codegen).generate()
        }
    }

    override fun generateSerialDesc(properties: List<PropertyDescriptor>) {
        codegen.v.newField(OtherOriginFir(codegen.myClass), ACC_PRIVATE or ACC_STATIC or ACC_FINAL or ACC_SYNTHETIC,
                   serialDescField, descType.descriptor, null, null)

        with(InstructionAdapter(codegen.createClInitMethodVisitor(codegen.descriptor))) {
            val classDescVar = 0
            visitCode()
            anew(descImplType)
            dup()
            aconst(serialName)
            invokespecial(descImplType.internalName, "<init>", "(Ljava/lang/String;)V", false)
            store(classDescVar, descImplType)
            for (property in properties) {
                load(classDescVar, descImplType)
                aconst(property.name.asString())
                invokevirtual(descImplType.internalName, "addElement", "(Ljava/lang/String;)V", false)
            }
            load(classDescVar, descImplType)
            putstatic(classAsmType.internalName, serialDescField, descType.descriptor)
            areturn(Type.VOID_TYPE)
            visitEnd()
        }
    }

    private fun InstructionAdapter.serialCLassDescToLocalVar(classDescVar: Int) {
        getstatic(classAsmType.internalName, serialDescField, descType.descriptor)
        store(classDescVar, descType)
    }

    // helper
    private fun generateMethod(function: FunctionDescriptor,
                               block: InstructionAdapter.(JvmMethodSignature, MethodContext) -> Unit) {
        codegen.functionCodegen.generateMethod(OtherOriginFir(codegen.myClass, function), function,
            object : FunctionGenerationStrategy() {
                override fun generateBody(
                        mv: MethodVisitor,
                        frameMap: FrameMap,
                        signature: JvmMethodSignature,
                        context: MethodContext,
                        parentCodegen: MemberCodegen<*>
                ) {
                    InstructionAdapter(mv).block(signature, context)
                }
            })
    }

    override fun generateCompanionSave(
            function: FunctionDescriptor, properties: List<PropertyDescriptor>
    ) {
        generateMethod(function) { signature, context ->
            // fun save(output: KOutput, obj : T)
            val outputVar = 1
            val objVar = 2
            val descVar = 3
            serialCLassDescToLocalVar(descVar)
            val outputType = signature.valueParameters[0].asmType
            val objType = signature.valueParameters[1].asmType
            // output.writeBegin(classDesc)
            load(outputVar, outputType)
            load(descVar, descType)
            invokevirtual(outputType.internalName, "writeBegin",
                               "(" + descType.descriptor + ")V", false)
            // loop for all properties
            for (index in properties.indices) {
                val property = properties[index]
                // output.writeXxxElementValue(classDesc, index, value)
                load(outputVar, outputType)
                load(descVar, descType)
                iconst(index)
                val propertyType = codegen.typeMapper.mapType(property.type)
                val sti = getSerialTypeInfo(property, propertyType)
                val useSerializer = stackValueSerializerInstance(sti)
                if (!sti.unit) codegen.genPropertyOnStack(this, context, property, objType, objVar)
                invokevirtual(outputType.internalName,
                                   "write" + sti.nn + (if (useSerializer) "Serializable" else "") + "ElementValue",
                                   "(" + descType.descriptor + "I" +
                                   (if (useSerializer) kSerialSaverType.descriptor else "") +
                                   (if (sti.unit) "" else sti.type.descriptor) + ")V", false)
            }
            // output.writeEnd(classDesc)
            load(outputVar, outputType)
            load(descVar, descType)
            invokevirtual(outputType.internalName, "writeEnd",
                               "(" + descType.descriptor + ")V", false)
            // return
            areturn(Type.VOID_TYPE)
        }
    }

    override fun generateCompanionLoad(
            function: FunctionDescriptor, properties: List<PropertyDescriptor>
    ) {
        generateMethod(function) { signature, _ ->
            // fun load(input: KInput): T
            val inputVar = 1
            val descVar = 2
            val indexVar = 3
            val readAllVar = 4
            val propsStartVar = 5
            serialCLassDescToLocalVar(descVar)
            val objType = signature.returnType
            val inputType = signature.valueParameters[0].asmType
            // boolean readAll = false
            iconst(0)
            store(readAllVar, Type.BOOLEAN_TYPE)
            // initialize all prop vars
            var propVar = propsStartVar
            for (property in properties) {
                val propertyType = codegen.typeMapper.mapType(property.type)
                stackValueDefault(propertyType)
                store(propVar, propertyType)
                propVar += propertyType.size
            }
            // input.readBegin(classDesc)
            load(inputVar, inputType)
            load(descVar, descType)
            invokevirtual(inputType.internalName, "readBegin",
                               "(" + descType.descriptor + ")V", false)
            // readElement: int index = input.readElement(classDesc)
            val readElementLabel = Label()
            visitLabel(readElementLabel)
            load(inputVar, inputType)
            load(descVar, descType)
            invokevirtual(inputType.internalName, "readElement",
                               "(" + descType.descriptor + ")I", false)
            store(indexVar, Type.INT_TYPE)
            // switch(index)
            val readAllLabel = Label()
            val readEndLabel = Label()
            val labels = arrayOfNulls<Label>(properties.size + 2)
            labels[0] = readAllLabel // READ_ALL
            labels[1] = readEndLabel // READ_DONE
            for (i in properties.indices) {
                labels[i + 2] = Label()
            }
            load(indexVar, Type.INT_TYPE)
            // todo: readEnd is currently default, should probably throw exception instead
            tableswitch(-2, properties.size - 1, readEndLabel, *labels)
            // readAll: readAll := true
            visitLabel(readAllLabel)
            iconst(1)
            store(readAllVar, Type.BOOLEAN_TYPE)
            // loop for all properties
            propVar = propsStartVar
            for (i in properties.indices) {
                val property = properties[i]
                // labelI: propX := input.readXxxValue(value)
                visitLabel(labels[i + 2])
                load(inputVar, inputType)
                load(descVar, descType)
                iconst(i)
                val propertyType = codegen.typeMapper.mapType(property.type)
                val sti = getSerialTypeInfo(property, propertyType)
                val useSerializer = stackValueSerializerInstance(sti)
                invokevirtual(inputType.internalName,
                                   "read" + sti.nn + (if (useSerializer) "Serializable" else "") + "ElementValue",
                                   "(" + descType.descriptor + "I" +
                                   (if (useSerializer) kSerialLoaderType.descriptor else "")
                                   + ")" + (if (sti.unit) "V" else sti.type.descriptor), false)
                if (!sti.unit) {
                    StackValue.coerce(sti.type, propertyType, this)
                    store(propVar, propertyType)
                }
                propVar += propertyType.size
                // if (readAll == false) goto readElement
                load(readAllVar, Type.BOOLEAN_TYPE)
                iconst(0)
                ificmpeq(readElementLabel)
            }
            // readEnd: input.readEnd(classDesc)
            visitLabel(readEndLabel)
            load(inputVar, inputType)
            load(descVar, descType)
            invokevirtual(inputType.internalName, "readEnd",
                               "(" + descType.descriptor + ")V", false)
            // create object
            anew(objType)
            dup()
            val constructorDesc = StringBuilder("(")
            propVar = propsStartVar
            for (property in properties) {
                val propertyType = codegen.typeMapper.mapType(property.type)
                constructorDesc.append(propertyType.descriptor)
                load(propVar, propertyType)
                propVar += propertyType.size
            }
            constructorDesc.append(")V")
            invokespecial(objType.internalName, "<init>", constructorDesc.toString(), false)
            // return
            areturn(objType)
        }
    }

    // todo: move to StackValue?
    private fun InstructionAdapter.stackValueDefault(type: Type) {
        when (type.sort) {
            Type.BOOLEAN, Type.BYTE, Type.SHORT, Type.CHAR, Type.INT -> iconst(0)
            Type.LONG -> lconst(0)
            Type.FLOAT -> fconst(0f)
            Type.DOUBLE -> dconst(0.0)
            else -> aconst(null)
        }
    }

    // returns false is property should not use serializer
    private fun InstructionAdapter.stackValueSerializerInstance(sti: SerialTypeInfo): Boolean {
        val serializer = sti.serializer ?: return false
        return stackValueSerializerInstance(sti.property.module, sti.property.type, serializer, this)
    }

    // returns false is cannot not use serializer
    //    use iv == null to check only (do not emit serializer onto stack)
    private fun stackValueSerializerInstance(module: ModuleDescriptor, kType: KotlinType, serializer: ClassDescriptor,
                                             iv: InstructionAdapter?): Boolean {
        if (serializer.kind == ClassKind.OBJECT) {
            // singleton serializer -- just get it
            if (iv != null)
                StackValue.singleton(serializer, codegen.typeMapper).put(kSerializerType, iv)
            return true
        }
        // serializer is not singleton object and shall be instantiated
        val argSerializers = kType.arguments.map { projection ->
            // bail out from stackValueSerializerInstance if any type argument is not serializable
            val argSerializer = findTypeSerializer(module, projection.type, codegen.typeMapper.mapType(projection.type)) ?: return false
            // check if it can be properly serialized with its args recursively
            if (!stackValueSerializerInstance(module, projection.type, argSerializer, null))
                return false
            Pair(projection.type, argSerializer)
        }
        // new serializer if needed
        iv?.apply {
            val serializerType = codegen.typeMapper.mapClass(serializer)
            anew(serializerType)
            dup()
            // instantiate all arg serializers on stack
            val signature = StringBuilder("(")
            if (serializer.classId == enumSerializerId) {
                // a special way to instantiate enum serializer
                aconst(codegen.typeMapper.mapType(kType))
                AsmUtil.wrapJavaClassIntoKClass(this)
                signature.append(AsmTypes.K_CLASS_TYPE.descriptor)
            }
            else {
                // all other serializers are instantiated with serializers of their generic types
                argSerializers.forEach { (argType, argSerializer) ->
                    assert(stackValueSerializerInstance(module, argType, argSerializer, this))
                    // wrap into nullable serializer if argType is nullable
                    if (argType.isMarkedNullable) {
                        invokestatic("kotlin/internal/BuiltinSerializersKt", "makeNullable",
                                     "(" + kSerializerType.descriptor + ")" + kSerializerType.descriptor, false)

                    }
                    signature.append(kSerializerType.descriptor)
                }
            }
            signature.append(")V")
            // invoke constructor
            invokespecial(serializerType.internalName, "<init>", signature.toString(), false)
            return true
        }
        return false // otherwise we were just checking -- return false
    }

    class SerialTypeInfo(
            val property: PropertyDescriptor,
            val type: Type,
            val nn: String,
            val serializer: ClassDescriptor? = null,
            val unit: Boolean = false
    )

    fun getSerialTypeInfo(property: PropertyDescriptor, type: Type): SerialTypeInfo {
        when (type.sort) {
            Type.BOOLEAN, Type.BYTE, Type.SHORT, Type.INT, Type.LONG, Type.FLOAT, Type.DOUBLE, Type.CHAR -> {
                val name = type.className
                return SerialTypeInfo(property, type, Character.toUpperCase(name[0]) + name.substring(1))
            }
            Type.OBJECT -> {
                // check for explicit serialization annotation on this property
                var serializer = property.propertySerializer.toClassDescriptor
                if (serializer == null) {
                    // no explicit serializer for this property. Check other built in types
                    if (KotlinBuiltIns.isString(property.type))
                        return SerialTypeInfo(property, Type.getType("Ljava/lang/String;"), "String")
                    if (KotlinBuiltIns.isUnit(property.type))
                        return SerialTypeInfo(property, Type.getType("Lkotlin/Unit;"), "Unit", unit = true)
                    // todo: more efficient enum support here, but only for enums that don't define custom serializer
                    // otherwise, it is a serializer for some other type
                    serializer = findTypeSerializer(property.module, property.type, type)
                }
                return SerialTypeInfo(property, Type.getType("Ljava/lang/Object;"),
                          if (property.type.isMarkedNullable) "Nullable" else "", serializer)
            }
            else -> throw AssertionError() // should not happen
        }
    }

    fun findTypeSerializer(module: ModuleDescriptor, kType: KotlinType, asmType: Type): ClassDescriptor? {
        return kType.typeSerializer.toClassDescriptor // check for serializer defined on the type
               ?: findStandardAsmTypeSerializer(module, asmType) // otherwise see if there is a standard serializer
               ?: findStandardKotlinTypeSerializer(module, kType)
    }

    fun findStandardKotlinTypeSerializer(module: ModuleDescriptor, kType: KotlinType): ClassDescriptor? {
        val classDescriptor = kType.constructor.declarationDescriptor as? ClassDescriptor ?: return null
        return if (classDescriptor.kind == ClassKind.ENUM_CLASS) module.findClassAcrossModuleDependencies(enumSerializerId) else null
    }

    fun findStandardAsmTypeSerializer(module: ModuleDescriptor, asmType: Type): ClassDescriptor? {
        val name = asmType.standardSerializer ?: return null
        return module.findClassAcrossModuleDependencies(ClassId(internalPackageFqName, Name.identifier(name)))
    }

    private val Type.standardSerializer: String? get() = when (this.className) {
        "kotlin.Unit" -> "UnitSerializer"
        "java.lang.Boolean" -> "BooleanSerializer"
        "java.lang.Byte" -> "ByteSerializer"
        "java.lang.Short" -> "ShortSerializer"
        "java.lang.Integer" -> "IntSerializer"
        "java.lang.Long" -> "LongSerializer"
        "java.lang.Float" -> "FloatSerializer"
        "java.lang.Double" -> "DoubleSerializer"
        "java.lang.Character" -> "CharSerializer"
        "java.lang.String" -> "StringSerializer"
        "java.util.List" -> "ListSerializer"
        else -> null
    }
}
