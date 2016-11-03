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

package test.serialization

import kotlin.serialization.*
import org.junit.Test

// Serializable data class

@KSerializable
data class Data(
        val value1: String,
        val value2: Int
)

// Serializable data class with explicit companion object

@KSerializable
data class DataExplicit(
        val value1: String,
        val value2: Int
) {
    companion object
}

// Regular (non-data) class with matching constructor

@KSerializable
class Reg(p1: String, p2: Int) {
    var value1: String = p1
    var value2: Int = p2
}

// Custom serializer

@KSerializable(CustomSerializer::class)
data class Custom(
        val _value1: String,
        val _value2: Int
)

object CustomSerializer : KSerializer<Custom> {
    val desc = object : KSerialClassDesc {
        override val name = "test.serialization.Custom"
        override val kind: KSerialClassKind = KSerialClassKind.CLASS
        override fun getElementCount(value: Any?) = 2
        override fun getElementName(index: Int) = when(index) {
            0 -> "value1"
            1 -> "value2"
            else -> ""
        }
        override fun getElementIndex(name: String) = when(name) {
            "value1" -> 0
            "value2" -> 1
            else -> -1
        }
    }

    override fun save(output: KOutput, obj : Custom) {
        output.writeBegin(desc)
        output.writeStringElementValue(desc, 0, obj._value1)
        output.writeIntElementValue(desc, 1, obj._value2)
        output.writeEnd(desc)
    }

    override fun load(input: KInput): Custom {
        input.readBegin(desc)
        if (input.readElement(desc) != 0) throw java.lang.IllegalStateException()
        val value1 = input.readStringElementValue(desc, 0)
        if (input.readElement(desc) != 1) throw java.lang.IllegalStateException()
        val value2 = input.readIntElementValue(desc, 1)
        if (input.readElement(desc) != KInput.READ_DONE) throw java.lang.IllegalStateException()
        input.readEnd(desc)
        return Custom(value1, value2)
    }
}

// --------- tests and utils ---------

class SerializeFlat() {
    @Test
    fun testData() {
        val out = Out("Data")
        out.write(Data, Data("s1", 42))
        out.done()

        val inp = Inp("Data")
        val data = inp.read(Data)
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testDataExplicit() {
        val out = Out("DataExplicit")
        out.write(DataExplicit, DataExplicit("s1", 42))
        out.done()

        val inp = Inp("DataExplicit")
        val data = inp.read(DataExplicit)
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testReg() {
        val out = Out("Reg")
        out.write(Reg, Reg("s1", 42))
        out.done()

        val inp = Inp("Reg")
        val data = inp.read(Reg)
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testCustom() {
        val out = Out("Custom")
        out.write(CustomSerializer, Custom("s1", 42))
        out.done()

        val inp = Inp("Custom")
        val data = inp.read(CustomSerializer)
        inp.done()
        assert(data._value1 == "s1" && data._value2 == 42)
    }

    fun fail(msg: String): Nothing = throw RuntimeException(msg)

    fun checkDesc(name: String, desc: KSerialClassDesc) {
        if (desc.name != "test.serialization." + name) fail("checkDesc name $desc")
        if (desc.kind != KSerialClassKind.CLASS) fail("checkDesc kind ${desc.kind}")
        if (desc.getElementName(0) != "value1") fail("checkDesc[0] $desc")
        if (desc.getElementName(1) != "value2") fail("checkDesc[1] $desc")
    }

    class Out(private val name: String) : ElementValueOutput() {
        var step = 0

        override fun writeBegin(desc: KSerialClassDesc) {
            checkDesc(name, desc)
            if (step == 0) step++ else fail("@$step: writeBegin($desc)")
        }

        override fun writeElement(desc: KSerialClassDesc, index: Int) {
            checkDesc(name, desc)
            when (step) {
                1 -> if (index == 0) { step++; return }
                3 -> if (index == 1) { step++; return }
            }
            fail("@$step: writeElement($desc, $index)")
        }

        override fun writeIntValue(value: Int) {
            when (step) {
                2 -> if (value == "s1") { step++; return }
            }
            fail("@$step: writeIntValue($value)")
        }

        override fun writeStringtValue(value: String) {
            when (step) {
                4 -> if (value == "s1") { step++; return }
            }
            fail("@$step: writeIntValue($value)")
        }

        override fun writeEnd(desc: KSerialClassDesc) {
            checkDesc(name, desc)
            if (step == 5) step++ else fail("@$step: writeEnd($desc)")
        }

        fun done() {
            if (step != 6) fail("@$step: OUT FAIL")
        }
    }

    class Inp(private val name: String) : ElementValueInput() {
        var step = 0

        override fun readBegin(desc: KSerialClassDesc) {
            checkDesc(name, desc)
            if (step == 0) step++ else fail("@$step: readBegin($desc)")
        }

        override fun readElement(desc: KSerialClassDesc): Int {
            checkDesc(name, desc)
            when (step) {
                1 -> { step++; return 0 }
                3 -> { step++; return 1 }
                5 -> { step++; return -1 }
            }
            fail("@$step: readElement($desc)")
        }

        override fun readStringValue(): String {
            when (step) {
                2 -> i{ step++; return "s1" }
            }
            fail("@$step: readStringValue()")
        }

        override fun readIntValue(): Int {
            when (step) {
                4 -> { step++; return 42 }
            }
            fail("@$step: readIntValue()")
        }

        override fun readEnd(desc: KSerialClassDesc) {
            checkDesc(name, desc)
            if (step == 6) step++ else fail("@$step: readEnd($desc)")
        }

        fun done() {
            if (step != 7) fail("@$step: INP FAIL")
        }
    }
}
