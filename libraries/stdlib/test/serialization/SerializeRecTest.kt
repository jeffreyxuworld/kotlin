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
data class Container(
        val data: Data
)

class SerializeRecTest {
    @Test
    fun testRec() {
        val out = Out()
        out.write(Container, Container(Data("s1", 42)))
        out.done()

        val inp = Inp()
        val box = inp.read(Container)
        inp.done()
    }

    fun fail(msg: String): Nothing = throw RuntimeException(msg)

    fun checkContainerDesc(desc: KSerialClassDesc) {
        if (desc.name != "test.serialization.Container") fail("checkContainerDesc name $desc")
        if (desc.getElementName(0) != "data") fail("checkContainerDesc $desc")
    }

    fun chekcDataDesc(desc: KSerialClassDesc) {
        if (desc.name != "test.serialization.Data") fail("chekcDataDesc name $desc")
        if (desc.getElementName(0) != "value1") fail("chekcDataDesc.0 $desc")
        if (desc.getElementName(1) != "value2") fail("chekcDataDesc.1 $desc")
    }

    class Out() : ElementValueOutput() {
        var step = 0

        override fun writeBegin(desc: KSerialClassDesc) {
            when(step) {
                0 -> { checkContainerDesc(desc); step++; return }
                3 -> { chekcDataDesc(desc); step++; return }
            }
            fail("@$step: writeBegin($desc)")
        }

        override fun writeElement(desc: KSerialClassDesc, index: Int) {
            when (step) {
                1 -> { chekcContainerDesc(desc); if (index == 0) { step++; return } }
                4 -> { chekcDataDesc(desc); if (index == 0) { step++; return } }
                6 -> { chekcDataDesc(desc); if (index == 1) { step++; return } }
            }
            fail("@$step: writeElement($desc, $index)")
        }

        override fun <T: Any> writeSerializableValue(saver: KSerialSaver<T>, value: T) {
            when (step) {
                2 -> { step++; saver.save(this, value); return }
            }
            fail("@$step: writeSerializableValue($value)")
        }

        override fun writeStringValue(value: String) {
            when (step) {
                5 -> { chekcDataDesc(desc); if (value == "s1") { step++; return } }
            }
            fail("@$step: writeStringValue($value)")
        }

        override fun writeIntValue(value: Int) {
            when (step) {
                7 -> { chekcDataDesc(desc); if (value == 42) { step++; return } }
            }
            fail("@$step: writeIntValue($value)")
        }

        override fun writeEnd(desc: KSerialClassDesc) {
            when(step) {
                8 -> { chekcDataDesc(desc); step++; return }
                9 -> { checkContainerDesc(desc); step++; return }
            }
            fail("@$step: writeEnd($desc)")
        }

        fun done() {
            if (step != 10) fail("@$step: OUT FAIL")
        }
    }

    class Inp() : ElementValueInput() {
        var step = 0

        override fun readBegin(desc: KSerialClassDesc) {
            when(step) {
                0 -> { checkContainerDesc(desc); step++; return }
                3 -> { chekcDataDesc(desc); step++; return }
            }
            fail("@$step: readBegin($desc)")
        }

        override fun readElement(desc: KSerialClassDesc): Int {
            when (step) {
                1 -> { checkContainerDesc(desc); step++; return 0 }
                4 -> { chekcDataDesc(desc); step++; return 0 }
                6 -> { chekcDataDesc(desc); step++; return 1 }
                8 -> { chekcDataDesc(desc); step++; return -1 }
                10 -> { checkContainerDesc(desc); step++; return -1 }
            }
            fail("@$step: readElement($desc)")
            return -1
        }

        override fun <T: Any> readSerializableValue(loader: KSerialLoader<T>): T {
            when (step) {
                2 -> { step++; return loader.load(this) }
            }
            fail("@$step: readSerializableElementValue()")
        }

        override fun readStringtValue(): String {
            when (step) {
                5 -> { step++; return "s1" }
            }
            fail("@$step: readStringValue()")
        }

        override fun readIntValue(): Int {
            when (step) {
                7 -> { step++; return 42 }
            }
            fail("@$step: readIntValue()")
        }

        override fun readEnd(desc: KSerialClassDesc) {
            when(step) {
                9 -> { chekcDataDesc(desc); step++; return }
                11 -> { checkContainerDesc(desc); step++; return }
            }
            fail("@$step: readEnd($desc)")
        }

        fun done() {
            if (step != 12) fail("@$step: INP FAIL")
        }
    }
}
