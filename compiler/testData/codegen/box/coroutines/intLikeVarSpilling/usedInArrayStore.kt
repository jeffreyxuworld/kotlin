// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND: JS

// WITH_RUNTIME
class Controller {
    suspend fun suspendHere(x: Continuation<Unit>) {
        x.resume(Unit)
    }
}

fun builder(coroutine c: Controller.() -> Continuation<Unit>) {
    c(Controller()).resume(Unit)
}

@JvmField
var booleanResult = booleanArrayOf()
@JvmField
var charResult = charArrayOf()
@JvmField
var byteResult = byteArrayOf()
@JvmField
var shortResult = shortArrayOf()
@JvmField
var intResult = intArrayOf()

fun box(): String {
    builder {
        val x = true
        suspendHere()
        val a = BooleanArray(1)
        a[0] = x
        booleanResult = a
    }

    if (!booleanResult[0]) return "fail 1"

    builder {
        val x = '1'
        suspendHere()
        val a = CharArray(1)
        a[0] = x
        charResult = a
    }

    if (charResult[0] != '1') return "fail 2"

    builder {
        val x: Byte = 1
        suspendHere()
        val a = ByteArray(1)
        a[0] = x
        byteResult = a
    }

    if (byteResult[0] != 1.toByte()) return "fail 3"

    builder {
        val x: Short = 1
        suspendHere()
        val a = ShortArray(1)
        a[0] = x
        shortResult = a
    }

    if (shortResult[0] != 1.toShort()) return "fail 4"

    builder {
        val x: Int = 1
        suspendHere()
        val a = IntArray(1)
        a[0] = x
        intResult = a
    }

    if (intResult[0] != 1) return "fail 5"
    return "OK"
}
