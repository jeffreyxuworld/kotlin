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

package kotlin.serialization

import kotlin.reflect.KClass
import kotlin.internal.UnitClassDesc

@Suppress("ANNOTATION_CLASS_WITH_BODY")
annotation class KSerializable(
    val value: KClass<out KSerializer<*>> = KSerializer::class // it means -- use default serializer by default
) {
    annotation class Name(val value: String)
}

enum class KSerialClassKind {
    CLASS, OBJECT, UNIT, SEALED, LIST, MAP, ENTRY
}

interface KSerialClassDesc {
    val name: String
    val kind: KSerialClassKind
    fun getElementCount(value: Any?): Int
    fun getElementName(index: Int): String
    fun getElementIndex(name: String): Int
}

interface KSerialSaver<in T> {
    fun save(output: KOutput, obj : T)
}

interface KSerialLoader<out T> {
    fun load(input: KInput): T
}

interface KSerializer<T>: KSerialSaver<T>, KSerialLoader<T>

class SerializationException(s: String) : RuntimeException(s)

// ========================================================================================================================

sealed class KOutput {
    // ------- top-level API (use it) -------

    fun <T : Any?> write(saver: KSerialSaver<T>, obj: T) { saver.save(this, obj) }

    fun <T : Any> writeNullable(saver: KSerialSaver<T>, obj: T?) {
        if (obj == null) {
            writeNullValue()
        } else {
            writeNotNullMark()
            saver.save(this, obj)
        }
    }

    // ------- low-level element value API for basic serializers -------

    // it is always invoked before writeXxxValue
    abstract fun writeElement(desc: KSerialClassDesc, index: Int)

    // will be followed by value
    abstract fun writeNotNullMark()

    // this is invoked after writeElement
    abstract fun writeNullValue()
    abstract fun writeValue(value: Any)
    abstract fun writeNullableValue(value: Any?): Unit
    abstract fun writeUnitValue()
    abstract fun writeBooleanValue(value: Boolean)
    abstract fun writeByteValue(value: Byte)
    abstract fun writeShortValue(value: Short)
    abstract fun writeIntValue(value: Int)
    abstract fun writeLongValue(value: Long)
    abstract fun writeFloatValue(value: Float)
    abstract fun writeDoubleValue(value: Double)
    abstract fun writeCharValue(value: Char)
    abstract fun writeStringValue(value: String)
    abstract fun <T : Enum<T>> writeEnumValue(enumClass: KClass<T>, value: T)

    inline fun <reified T : Enum<T>> writeEnumValue(value: T) = writeEnumValue(T::class, value)

    open fun <T : Any?> writeSerializableValue(saver: KSerialSaver<T>, value: T) {
        saver.save(this, value)
    }

    fun <T : Any> writeNullableSerializableValue(saver: KSerialSaver<T>, value: T?) {
        if (value == null) {
            writeNullValue()
        } else {
            writeNotNullMark()
            writeSerializableValue(saver, value)
        }
    }

    // -------------------------------------------------------------------------------------
    // methods below this line are invoked by compiler-generated KSerializer implementation

    // composite value delimiter api (writeEnd ends composite object)
    abstract fun writeBegin(desc: KSerialClassDesc)
    abstract fun writeEnd(desc: KSerialClassDesc)

    abstract fun writeElementValue(desc: KSerialClassDesc, index: Int, value: Any)
    abstract fun writeNullableElementValue(desc: KSerialClassDesc, index: Int, value: Any?)

    abstract fun writeUnitElementValue(desc: KSerialClassDesc, index: Int)
    abstract fun writeBooleanElementValue(desc: KSerialClassDesc, index: Int, value: Boolean)
    abstract fun writeByteElementValue(desc: KSerialClassDesc, index: Int, value: Byte)
    abstract fun writeShortElementValue(desc: KSerialClassDesc, index: Int, value: Short)
    abstract fun writeIntElementValue(desc: KSerialClassDesc, index: Int, value: Int)
    abstract fun writeLongElementValue(desc: KSerialClassDesc, index: Int, value: Long)
    abstract fun writeFloatElementValue(desc: KSerialClassDesc, index: Int, value: Float)
    abstract fun writeDoubleElementValue(desc: KSerialClassDesc, index: Int, value: Double)
    abstract fun writeCharElementValue(desc: KSerialClassDesc, index: Int, value: Char)
    abstract fun writeStringElementValue(desc: KSerialClassDesc, index: Int, value: String)
    abstract fun <T : Enum<T>> writeEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>, value: T)

    inline fun <reified T : Enum<T>> writeEnumElementValue(desc: KSerialClassDesc, index: Int, value: T) {
        writeEnumElementValue(desc, index, T::class, value)
    }

    fun <T : Any?> writeSerializableElementValue(desc: KSerialClassDesc, index: Int, saver: KSerialSaver<T>, value: T) {
        writeElement(desc, index)
        writeSerializableValue(saver, value)
    }

    fun <T : Any> writeNullableSerializableElementValue(desc: KSerialClassDesc, index: Int, saver: KSerialSaver<T>, value: T?) {
        writeElement(desc, index)
        writeNullableSerializableValue(saver, value)
    }
}

sealed class KInput {
    // ------- top-level API (use it) -------

    fun <T : Any?> read(loader: KSerialLoader<T>): T = loader.load(this)
    fun <T : Any> readNullable(loader: KSerialLoader<T>): T? = if (readNotNullMark()) read(loader) else null

    // ------- low-level element value API for basic serializers -------

    // returns true if the following value is not null, false if not null (consumes null value)
    abstract fun readNotNullMark(): Boolean

    abstract fun readValue(): Any
    abstract fun readNullableValue(): Any?
    abstract fun readUnitValue()
    abstract fun readBooleanValue(): Boolean
    abstract fun readByteValue(): Byte
    abstract fun readShortValue(): Short
    abstract fun readIntValue(): Int
    abstract fun readLongValue(): Long
    abstract fun readFloatValue(): Float
    abstract fun readDoubleValue(): Double
    abstract fun readCharValue(): Char
    abstract fun readStringValue(): String
    abstract fun <T : Enum<T>> readEnumValue(enumClass: KClass<T> ): T

    inline fun <reified T : Enum<T>> readEnumValue(): T = readEnumValue(T::class)

    fun <T : Any?> readSerializableValue(loader: KSerialLoader<T>): T = loader.load(this)

    fun <T : Any> readNullableSerializableValue(loader: KSerialLoader<T>): T? =
            if (readNotNullMark()) readSerializableValue(loader) else null

    // -------------------------------------------------------------------------------------
    // methods below this line are invoked by compiler-generated KSerializer implementation

    // composite value delimiter api (writeEnd ends composite object)
    abstract fun readBegin(desc: KSerialClassDesc)
    abstract fun readEnd(desc: KSerialClassDesc)

    // readElement results
    companion object {
        const val READ_DONE = -1
        const val READ_ALL = -2
    }

    // returns either index or one of READ_XXX constants
    abstract fun readElement(desc: KSerialClassDesc): Int

    abstract fun readElementValue(desc: KSerialClassDesc, index: Int): Any
    abstract fun readNullableElementValue(desc: KSerialClassDesc, index: Int): Any?
    abstract fun readUnitElementValue(desc: KSerialClassDesc, index: Int)
    abstract fun readBooleanElementValue(desc: KSerialClassDesc, index: Int): Boolean
    abstract fun readByteElementValue(desc: KSerialClassDesc, index: Int): Byte
    abstract fun readShortElementValue(desc: KSerialClassDesc, index: Int): Short
    abstract fun readIntElementValue(desc: KSerialClassDesc, index: Int): Int
    abstract fun readLongElementValue(desc: KSerialClassDesc, index: Int): Long
    abstract fun readFloatElementValue(desc: KSerialClassDesc, index: Int): Float
    abstract fun readDoubleElementValue(desc: KSerialClassDesc, index: Int): Double
    abstract fun readCharElementValue(desc: KSerialClassDesc, index: Int): Char
    abstract fun readStringElementValue(desc: KSerialClassDesc, index: Int): String
    abstract fun <T : Enum<T>> readEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>): T

    inline fun <reified T : Enum<T>> readEnumElementValue(desc: KSerialClassDesc, index: Int): T =
            readEnumElementValue(desc, index, T::class)

    abstract fun <T : Any?> readSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T
    abstract fun <T : Any> readNullableSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T?
}

// ========================================================================================================================

open class ElementValueOutput : KOutput() {
    // ------- implementation API -------

    // it is always invoked before writeXxxValue
    override fun writeElement(desc: KSerialClassDesc, index: Int) {}

    // override for a special representation of nulls if needed (empty object by default)
    override fun writeNotNullMark() {}

    // writes an arbitrary non-null value
    override fun writeValue(value: Any) { throw SerializationException("value is not supported") }

    override final fun writeNullableValue(value: Any?) {
        if (value == null) {
            writeNullValue()
        } else {
            writeNotNullMark()
            writeValue(value)
        }
    }

    override fun writeNullValue() { throw SerializationException("null is not supported") }
    override fun writeUnitValue() { writeBegin(UnitClassDesc); writeEnd(UnitClassDesc) }

    // type-specific value-based output, override for performance and custom type representations
    override fun writeBooleanValue(value: Boolean) = writeValue(value)
    override fun writeByteValue(value: Byte) = writeValue(value)
    override fun writeShortValue(value: Short) = writeValue(value)
    override fun writeIntValue(value: Int) = writeValue(value)
    override fun writeLongValue(value: Long) = writeValue(value)
    override fun writeFloatValue(value: Float) = writeValue(value)
    override fun writeDoubleValue(value: Double) = writeValue(value)
    override fun writeCharValue(value: Char) = writeValue(value)
    override fun writeStringValue(value: String) = writeValue(value)
    override fun <T : Enum<T>> writeEnumValue(enumClass: KClass<T>, value: T) = writeValue(value)

    // composite value delimiter api
    override fun writeBegin(desc: KSerialClassDesc) {}
    override fun writeEnd(desc: KSerialClassDesc) {}

    // -------------------------------------------------------------------------------------

    override final fun writeElementValue(desc: KSerialClassDesc, index: Int, value: Any) { writeElement(desc, index); writeValue(value) }
    override final fun writeNullableElementValue(desc: KSerialClassDesc, index: Int, value: Any?) { writeElement(desc, index); writeNullableValue(value) }
    override final fun writeUnitElementValue(desc: KSerialClassDesc, index: Int) { writeElement(desc, index); writeUnitValue() }
    override final fun writeBooleanElementValue(desc: KSerialClassDesc, index: Int, value: Boolean) { writeElement(desc, index); writeBooleanValue(value) }
    override final fun writeByteElementValue(desc: KSerialClassDesc, index: Int, value: Byte) { writeElement(desc, index); writeByteValue(value) }
    override final fun writeShortElementValue(desc: KSerialClassDesc, index: Int, value: Short) { writeElement(desc, index); writeShortValue(value) }
    override final fun writeIntElementValue(desc: KSerialClassDesc, index: Int, value: Int) { writeElement(desc, index); writeIntValue(value) }
    override final fun writeLongElementValue(desc: KSerialClassDesc, index: Int, value: Long) { writeElement(desc, index); writeLongValue(value) }
    override final fun writeFloatElementValue(desc: KSerialClassDesc, index: Int, value: Float) { writeElement(desc, index); writeFloatValue(value) }
    override final fun writeDoubleElementValue(desc: KSerialClassDesc, index: Int, value: Double) { writeElement(desc, index); writeDoubleValue(value) }
    override final fun writeCharElementValue(desc: KSerialClassDesc, index: Int, value: Char) { writeElement(desc, index); writeCharValue(value) }
    override final fun writeStringElementValue(desc: KSerialClassDesc, index: Int, value: String) { writeElement(desc, index); writeStringValue(value) }
    override final fun <T : Enum<T>> writeEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>, value: T) { writeElement(desc, index); writeEnumValue(enumClass, value) }
}

open class ElementValueInput : KInput() {
    // ------- implementation API -------

    // unordered read api, override to read props in arbitrary order
    override fun readElement(desc: KSerialClassDesc): Int = READ_ALL

    // returns true if the following value is not null, false if not null (consumes null value)
    override fun readNotNullMark(): Boolean = true

    override fun readValue(): Any { throw SerializationException("value is not supported") }
    override fun readNullableValue(): Any? = if (readNotNullMark()) readValue() else null
    override fun readUnitValue() { readBegin(UnitClassDesc); readEnd(UnitClassDesc) }

    // type-specific value-based input, override for performance and custom type representations
    override fun readBooleanValue(): Boolean = readValue() as Boolean
    override fun readByteValue(): Byte = readValue() as Byte
    override fun readShortValue(): Short = readValue() as Short
    override fun readIntValue(): Int = readValue() as Int
    override fun readLongValue(): Long = readValue() as Long
    override fun readFloatValue(): Float = readValue() as Float
    override fun readDoubleValue(): Double = readValue() as Double
    override fun readCharValue(): Char = readValue() as Char
    override fun readStringValue(): String = readValue() as String

    @Suppress("UNCHECKED_CAST")
    override fun <T : Enum<T>> readEnumValue(enumClass: KClass<T> ): T =
            readValue() as T
            // todo: enumClass.cast(readValue())

    // composite value delimiter api
    override fun readBegin(desc: KSerialClassDesc) {}
    override fun readEnd(desc: KSerialClassDesc) {}

    // -------------------------------------------------------------------------------------

    override final fun readElementValue(desc: KSerialClassDesc, index: Int): Any = readValue()
    override final fun readNullableElementValue(desc: KSerialClassDesc, index: Int): Any? = readNullableValue()
    override final fun readUnitElementValue(desc: KSerialClassDesc, index: Int) = readUnitValue()
    override final fun readBooleanElementValue(desc: KSerialClassDesc, index: Int): Boolean = readBooleanValue()
    override final fun readByteElementValue(desc: KSerialClassDesc, index: Int): Byte = readByteValue()
    override final fun readShortElementValue(desc: KSerialClassDesc, index: Int): Short = readShortValue()
    override final fun readIntElementValue(desc: KSerialClassDesc, index: Int): Int = readIntValue()
    override final fun readLongElementValue(desc: KSerialClassDesc, index: Int): Long = readLongValue()
    override final fun readFloatElementValue(desc: KSerialClassDesc, index: Int): Float = readFloatValue()
    override final fun readDoubleElementValue(desc: KSerialClassDesc, index: Int): Double = readDoubleValue()
    override final fun readCharElementValue(desc: KSerialClassDesc, index: Int): Char = readCharValue()
    override final fun readStringElementValue(desc: KSerialClassDesc, index: Int): String = readStringValue()
    override final fun <T : Enum<T>> readEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>): T = readEnumValue(enumClass)

    override final fun <T: Any?> readSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T =
            readSerializableValue(loader)

    override final fun <T: Any> readNullableSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T? =
            readNullableSerializableValue(loader)
}

// ========================================================================================================================

open class NamedValueOutput(rootName: String = "") : KOutput() {
    // ------- API (override it) -------

    open fun writeNamed(name: String, value: Any) { throw SerializationException("value is not supported for $name") }
    open fun writeNamedNull(name: String) { throw SerializationException("null is not supported for $name") }
    open fun writeNamedUnit(name: String) = writeNamed(name, Unit)
    open fun writeNamedBoolean(name: String, value: Boolean) = writeNamed(name, value)
    open fun writeNamedByte(name: String, value: Byte) = writeNamed(name, value)
    open fun writeNamedShort(name: String, value: Short) = writeNamed(name, value)
    open fun writeNamedInt(name: String, value: Int) = writeNamed(name, value)
    open fun writeNamedLong(name: String, value: Long) = writeNamed(name, value)
    open fun writeNamedFloat(name: String, value: Float) = writeNamed(name, value)
    open fun writeNamedDouble(name: String, value: Double) = writeNamed(name, value)
    open fun writeNamedChar(name: String, value: Char) = writeNamed(name, value)
    open fun writeNamedString(name: String, value: String) = writeNamed(name, value)
    open fun <T : Enum<T>> writeNamedEnum(name: String, enumClass: KClass<T>, value: T) = writeNamed(name, value)

    open fun <T : Any?> isNamedSerializableRecursive(name: String, saver: KSerialSaver<T>, value: T): Boolean = true

    // ---------------

    private fun writeNamedNullable(name: String, value: Any?) {
        if (value == null) writeNamedNull(name) else writeNamed(name, value)
    }

    // ------- implementation -------

    override final fun writeElement(desc: KSerialClassDesc, index: Int) { pushName(name(desc, index)) }
    override final fun writeNotNullMark() {}
    override final fun writeNullValue() { writeNamedNull(popName()) }
    override final fun writeValue(value: Any) { writeNamed(popName(), value) }
    override final fun writeNullableValue(value: Any?) { writeNamedNullable(popName(), value) }
    override final fun writeUnitValue() { writeNamedUnit(popName()) }
    override final fun writeBooleanValue(value: Boolean) { writeNamedBoolean(popName(), value) }
    override final fun writeByteValue(value: Byte) { writeNamedByte(popName(), value) }
    override final fun writeShortValue(value: Short) { writeNamedShort(popName(), value) }
    override final fun writeIntValue(value: Int) { writeNamedInt(popName(), value) }
    override final fun writeLongValue(value: Long) { writeNamedLong(popName(), value) }
    override final fun writeFloatValue(value: Float) { writeNamedFloat(popName(), value) }
    override final fun writeDoubleValue(value: Double) { writeNamedDouble(popName(), value) }
    override final fun writeCharValue(value: Char) { writeNamedChar(popName(), value) }
    override final fun writeStringValue(value: String) { writeNamedString(popName(), value) }
    override final fun <T : Enum<T>> writeEnumValue(enumClass: KClass<T>, value: T) { writeNamedEnum(popName(), enumClass, value) }

    override final fun <T : Any?> writeSerializableValue(saver: KSerialSaver<T>, value: T) {
        if (isNamedSerializableRecursive(currentName(), saver, value))
            saver.save(this, value)
        else
            writeNullableValue(value)
    }

    // ---------------

    override final fun writeBegin(desc: KSerialClassDesc) {}
    override final fun writeEnd(desc: KSerialClassDesc) { popName() }

    override final fun writeElementValue(desc: KSerialClassDesc, index: Int, value: Any) = writeNamed(name(desc, index), value)
    override final fun writeNullableElementValue(desc: KSerialClassDesc, index: Int, value: Any?) = writeNamedNullable(name(desc, index), value)
    override final fun writeUnitElementValue(desc: KSerialClassDesc, index: Int) = writeNamedUnit(name(desc, index))
    override final fun writeBooleanElementValue(desc: KSerialClassDesc, index: Int, value: Boolean) = writeNamedBoolean(name(desc, index), value)
    override final fun writeByteElementValue(desc: KSerialClassDesc, index: Int, value: Byte) = writeNamedByte(name(desc, index), value)
    override final fun writeShortElementValue(desc: KSerialClassDesc, index: Int, value: Short) = writeNamedShort(name(desc, index), value)
    override final fun writeIntElementValue(desc: KSerialClassDesc, index: Int, value: Int) = writeNamedInt(name(desc, index), value)
    override final fun writeLongElementValue(desc: KSerialClassDesc, index: Int, value: Long) = writeNamedLong(name(desc, index), value)
    override final fun writeFloatElementValue(desc: KSerialClassDesc, index: Int, value: Float) = writeNamedFloat(name(desc, index), value)
    override final fun writeDoubleElementValue(desc: KSerialClassDesc, index: Int, value: Double) = writeNamedDouble(name(desc, index), value)
    override final fun writeCharElementValue(desc: KSerialClassDesc, index: Int, value: Char) = writeNamedChar(name(desc, index), value)
    override final fun writeStringElementValue(desc: KSerialClassDesc, index: Int, value: String) = writeNamedString(name(desc, index), value)

    override final fun <T : Enum<T>> writeEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>, value: T) =
            writeNamedEnum(name(desc, index), enumClass, value)

    // ---------------

    private val nameStack = arrayListOf(rootName)
    private fun currentName(): String = nameStack.last()
    private fun pushName(name: String) { nameStack.add(name) }
    private fun popName() = nameStack.removeAt(nameStack.lastIndex)
    private fun name(desc: KSerialClassDesc, index: Int) = composeName(currentName(), elementName(desc, index))

    open fun elementName(desc: KSerialClassDesc, index: Int) = desc.getElementName(index)
    open fun composeName(parentName: String, childName: String) = if (parentName.isEmpty()) childName else parentName + "." + childName
}

open class NamedValueInput(rootName: String = "") : KInput() {
    // ------- API (override it) -------

    open fun readNamed(name: String): Any { throw SerializationException("value is not supported for $name") }
    open fun readNamedNotNullMark(name: String): Boolean = true
    open fun readNamedUnit(name: String): Unit = readNamed(name) as Unit
    open fun readNamedBoolean(name: String): Boolean = readNamed(name) as Boolean
    open fun readNamedByte(name: String): Byte = readNamed(name) as Byte
    open fun readNamedShort(name: String): Short = readNamed(name) as Short
    open fun readNamedInt(name: String): Int = readNamed(name) as Int
    open fun readNamedLong(name: String): Long = readNamed(name) as Long
    open fun readNamedFloat(name: String): Float = readNamed(name) as Float
    open fun readNamedDouble(name: String): Double = readNamed(name) as Double
    open fun readNamedChar(name: String): Char = readNamed(name) as Char
    open fun readNamedString(name: String): String = readNamed(name) as String

    @Suppress("UNCHECKED_CAST")
    open fun <T : Enum<T>> readNamedEnum(name: String, enumClass: KClass<T>): T =
            readNamed(name) as T
            // todo: enumClass.cast(readNamed(name))

    private fun readNamedNullable(name: String): Any? = if (readNamedNotNullMark(name)) readNamed(name) else null

    // ------- implementation -------

    override final fun readNotNullMark(): Boolean = readNamedNotNullMark(currentName())
    override final fun readValue(): Any = readNamed(popName())
    override final fun readNullableValue(): Any? = readNamedNullable(popName())
    override final fun readUnitValue() = readNamedUnit(popName())
    override final fun readBooleanValue(): Boolean = readNamedBoolean(popName())
    override final fun readByteValue(): Byte = readNamedByte(popName())
    override final fun readShortValue(): Short = readNamedShort(popName())
    override final fun readIntValue(): Int = readNamedInt(popName())
    override final fun readLongValue(): Long = readNamedLong(popName())
    override final fun readFloatValue(): Float = readNamedFloat(popName())
    override final fun readDoubleValue(): Double = readNamedDouble(popName())
    override final fun readCharValue(): Char = readNamedChar(popName())
    override final fun readStringValue(): String = readNamedString(popName())
    override final fun <T : Enum<T>> readEnumValue(enumClass: KClass<T> ): T = readNamedEnum(popName(), enumClass)

    // ---------------

    override final fun readBegin(desc: KSerialClassDesc) {}
    override final fun readEnd(desc: KSerialClassDesc) { popName() }

    override final fun readElement(desc: KSerialClassDesc): Int = READ_ALL

    override final fun readElementValue(desc: KSerialClassDesc, index: Int): Any = readNamed(name(desc, index))
    override final fun readNullableElementValue(desc: KSerialClassDesc, index: Int): Any? = readNamedNullable(name(desc, index))
    override final fun readUnitElementValue(desc: KSerialClassDesc, index: Int) = readNamedUnit(name(desc, index))
    override final fun readBooleanElementValue(desc: KSerialClassDesc, index: Int): Boolean = readNamedBoolean(name(desc, index))
    override final fun readByteElementValue(desc: KSerialClassDesc, index: Int): Byte = readNamedByte(name(desc, index))
    override final fun readShortElementValue(desc: KSerialClassDesc, index: Int): Short = readNamedShort(name(desc, index))
    override final fun readIntElementValue(desc: KSerialClassDesc, index: Int): Int = readNamedInt(name(desc, index))
    override final fun readLongElementValue(desc: KSerialClassDesc, index: Int): Long = readNamedLong(name(desc, index))
    override final fun readFloatElementValue(desc: KSerialClassDesc, index: Int): Float = readNamedFloat(name(desc, index))
    override final fun readDoubleElementValue(desc: KSerialClassDesc, index: Int): Double = readNamedDouble(name(desc, index))
    override final fun readCharElementValue(desc: KSerialClassDesc, index: Int): Char = readNamedChar(name(desc, index))
    override final fun readStringElementValue(desc: KSerialClassDesc, index: Int): String = readNamedString(name(desc, index))
    override final fun <T : Enum<T>> readEnumElementValue(desc: KSerialClassDesc, index: Int, enumClass: KClass<T>): T = readNamedEnum(name(desc, index), enumClass)

    override final fun <T: Any?> readSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T {
        pushName(name(desc, index))
        return readSerializableValue(loader)
    }

    override final fun <T: Any> readNullableSerializableElementValue(desc: KSerialClassDesc, index: Int, loader: KSerialLoader<T>): T? {
        pushName(name(desc, index))
        return readNullableSerializableValue(loader)
    }

    // ---------------

    private val nameStack = arrayListOf(rootName)
    private fun currentName(): String = nameStack.last()
    private fun pushName(name: String) { nameStack.add(name) }
    private fun popName() = nameStack.removeAt(nameStack.lastIndex)
    private fun name(desc: KSerialClassDesc, index: Int) = composeName(currentName(), elementName(desc, index))

    open fun elementName(desc: KSerialClassDesc, index: Int) = desc.getElementName(index)
    open fun composeName(parentName: String, childName: String) = if (parentName.isEmpty()) childName else parentName + "." + childName
}