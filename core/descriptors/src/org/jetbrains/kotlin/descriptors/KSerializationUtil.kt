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

@file:JvmName("KSerializationUtil")

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.KotlinBuiltIns.isConstructedFromGivenClass
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.serialization.deserialization.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.types.*

val packageFqName = FqName("kotlin.serialization")

val serializableName = Name.identifier("KSerializable")
val serializerName = Name.identifier("KSerializer")

val serializableFqName = packageFqName.child(serializableName)
val serializerFqName = packageFqName.child(serializerName)

fun ClassDescriptor.getKSerializer(): ClassDescriptor =
        module.findClassAcrossModuleDependencies(ClassId(packageFqName, serializerName))!!

fun ClassDescriptor.getKSerializerType(argument: SimpleType): SimpleType {
    val projectionType = Variance.INVARIANT
    val types = listOf(TypeProjectionImpl(projectionType, argument))
    return KotlinTypeFactory.simpleNotNullType(Annotations.EMPTY, getKSerializer(), types)
}

fun isKSerializer(type: KotlinType?): Boolean =
        type != null && !type.isMarkedNullable && isConstructedFromGivenClass(type, serializerFqName)

val KotlinType?.toClassDescriptor: ClassDescriptor?
    @JvmName("toClassDescriptor")
    get() = this?.constructor?.declarationDescriptor as? ClassDescriptor

internal val Annotations.serializer: KotlinType?
    get() = findAnnotation(serializableFqName)?.let { annotation ->
        annotation.allValueArguments.entries.singleOrNull { it.key.name.asString() == "value" }?.value?.let { value ->
            value.value as? KotlinType
        }
    }

internal val ClassDescriptor.isDefaultSerializable: Boolean
    get() = annotations.hasAnnotation(serializableFqName) && annotations.serializer == null

// serializer that was declared for this type
internal val ClassDescriptor?.classSerializer: KotlinType?
    get() = this?.let {
        // serializer annotation on class?
        annotations.serializer?.let { return it }
        // default serializable?
        if (isDefaultSerializable) return companionObjectDescriptor?.defaultType
        return null
    }

// serializer that was declared for this specific type or annotation from a class declaration
val KotlinType?.typeSerializer: KotlinType?
    get() = this?.let {
        // serializer annotation on this type or from a class
        return it.annotations.serializer ?: (it.constructor.declarationDescriptor as? ClassDescriptor).classSerializer
    }

// serializer that was declared specifically for this property via its own annotation or via annotation on its type
val PropertyDescriptor.propertySerializer: KotlinType?
    get() = annotations.serializer ?: type.typeSerializer


val ClassDescriptor.serializableProperties: List<PropertyDescriptor>
    get() = unsubstitutedMemberScope.getContributedDescriptors(DescriptorKindFilter.VARIABLES)
            .filterIsInstance<PropertyDescriptor>()

fun getSerializableClassDescriptor(companionDescriptor: ClassDescriptor) : ClassDescriptor? {
    if (!companionDescriptor.isCompanionObject) return null
    val classDescriptor = (companionDescriptor.containingDeclaration as? ClassDescriptor) ?: return null
    if (!classDescriptor.isDefaultSerializable) return null
    return classDescriptor
}

fun isSerializerCompanion(companionDescriptor: ClassDescriptor) : Boolean = getSerializableClassDescriptor(companionDescriptor) != null

// todo: serialization: do an actual check better that just number of parameters
fun ClassDescriptor.checkSaveMethodParameters(parameters: List<ValueParameterDescriptor>) : Boolean =
        parameters.size == 2

fun ClassDescriptor.checkSaveMethodResult(type: KotlinType) : Boolean =
        KotlinBuiltIns.isUnit(type)

// todo: serialization: do an actual check better that just number of parameters
fun ClassDescriptor.checkLoadMethodParameters(parameters: List<ValueParameterDescriptor>) : Boolean =
        parameters.size == 1

// todo: serialization: do an actual check
fun ClassDescriptor.checkLoadMethodResult(type: KotlinType) : Boolean =
        true
