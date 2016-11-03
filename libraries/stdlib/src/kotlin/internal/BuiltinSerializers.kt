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

package kotlin.internal

import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.serialization.*

object UnitClassDesc : KSerialClassDesc {
    override val name: String = "Unit"
    override val kind: KSerialClassKind = KSerialClassKind.UNIT
    override fun getElementCount(value: Any?): Int = 0
    override fun getElementName(index: Int): String { throw SerializationException("Unit has no elements") }
    override fun getElementIndex(name: String): Int { throw SerializationException("Unit has no elements") }
}

object UnitSerializer : KSerializer<Unit> {
    override fun save(output: KOutput, obj: Unit) = output.writeUnitValue()
    override fun load(input: KInput): Unit = input.readUnitValue()
}

object BooleanSerializer : KSerializer<Boolean> {
    override fun save(output: KOutput, obj: Boolean) = output.writeBooleanValue(obj)
    override fun load(input: KInput): Boolean = input.readBooleanValue()
}

object ByteSerializer : KSerializer<Byte> {
    override fun save(output: KOutput, obj: Byte) = output.writeByteValue(obj)
    override fun load(input: KInput): Byte = input.readByteValue()
}

object ShortSerializer : KSerializer<Short> {
    override fun save(output: KOutput, obj: Short) = output.writeShortValue(obj)
    override fun load(input: KInput): Short = input.readShortValue()
}

object IntSerializer : KSerializer<Int> {
    override fun save(output: KOutput, obj: Int) = output.writeIntValue(obj)
    override fun load(input: KInput): Int = input.readIntValue()
}

object LongSerializer : KSerializer<Long> {
    override fun save(output: KOutput, obj: Long) = output.writeLongValue(obj)
    override fun load(input: KInput): Long = input.readLongValue()
}

object FloatSerializer : KSerializer<Float> {
    override fun save(output: KOutput, obj: Float) = output.writeFloatValue(obj)
    override fun load(input: KInput): Float = input.readFloatValue()
}

object DoubleSerializer : KSerializer<Double> {
    override fun save(output: KOutput, obj: Double) = output.writeDoubleValue(obj)
    override fun load(input: KInput): Double = input.readDoubleValue()
}

object CharSerializer : KSerializer<Char> {
    override fun save(output: KOutput, obj: Char) = output.writeCharValue(obj)
    override fun load(input: KInput): Char = input.readCharValue()
}

object StringSerializer : KSerializer<String> {
    override fun save(output: KOutput, obj: String) = output.writeStringValue(obj)
    override fun load(input: KInput): String = input.readStringValue()
}

// note, that it is instantiated in a special way
class EnumSerializer<T : Enum<T>>(private val enumClass: KClass<T>) : KSerializer<T> {
    override fun save(output: KOutput, obj: T) = output.writeEnumValue(enumClass, obj)
    override fun load(input: KInput): T = input.readEnumValue(enumClass)
}

fun <T : Any> makeNullable(element: KSerializer<T>): KSerializer<T?> = NullableSerializer(element)

class NullableSerializer<T : Any>(private val element: KSerializer<T>) : KSerializer<T?> {
    override fun save(output: KOutput, obj: T?) {
        if (obj != null) {
            output.writeNotNullMark();
            element.save(output, obj)
        } else {
            output.writeNullValue();
        }
    }

    override fun load(input: KInput): T? = if (input.readNotNullMark()) element.load(input) else null
}

const val SIZE_INDEX = 0

class ListSerializer<T>(private val element: KSerializer<T>) : KSerializer<List<T>> {
    override fun save(output: KOutput, obj: List<T>) {
        output.writeBegin(ListClassDesc)
        val size = obj.size
        output.writeIntElementValue(ListClassDesc, SIZE_INDEX, size)
        for (index in 1..size)
            output.writeSerializableElementValue(ListClassDesc, index, element, obj[index - 1])
        output.writeEnd(ListClassDesc)
    }

    override fun load(input: KInput): List<T> {
        input.readBegin(ListClassDesc)
        val result = arrayListOf<T>()
        mainLoop@ while (true) {
            val index = input.readElement(ListClassDesc)
            when (index) {
                KInput.READ_ALL -> {
                    readAll(input, result)
                    break@mainLoop
                }
                KInput.READ_DONE -> {
                    break@mainLoop
                }
                SIZE_INDEX -> {
                    readSize(input, result)
                }
                else -> {
                    if (result.size == index - 1)
                        readItem(input, result, index)
                    else
                        throw SerializationException("List elements should be in order, unexpected index $index")
                }
            }

        }
        input.readEnd(ListClassDesc)
        return result
    }

    private fun readSize(input: KInput, result: ArrayList<T>): Int {
        val size = input.readIntElementValue(ListClassDesc, SIZE_INDEX)
        result.ensureCapacity(size)
        return size
    }

    private fun readItem(input: KInput, result: ArrayList<T>, index: Int) {
        result.add(input.readSerializableElementValue(ListClassDesc, index, element))
    }

    private fun readAll(input: KInput, result: ArrayList<T>) {
        val size = readSize(input, result)
        for (index in 1..size)
            readItem(input, result, index)
    }
}

object ListClassDesc : KSerialClassDesc {
    override val name: String get() = "kotlin.collections.List"
    override val kind: KSerialClassKind get() = KSerialClassKind.LIST
    override fun getElementCount(value: Any?): Int = (value as? List<*>)?.size ?: 0
    override fun getElementName(index: Int): String = if (index == SIZE_INDEX) "size" else index.toString()
    override fun getElementIndex(name: String): Int = if (name == "size") SIZE_INDEX else parseInt(name)

    // todo: kludge: becaues JS does not have String.toInt() KT-4497
    private fun parseInt(name: String): Int {
        check(name != "") { "empty name" }
        var result = 0
        for (c in name) {
            check(c in '0'..'9') { "Invalid digit $c" }
            result = result * 10 + (c.toInt() - '0'.toInt())
        }
        return result
    }
}

