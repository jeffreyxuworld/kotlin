// WITH_RUNTIME

class Controller {
    var result = ""

    suspend fun <T> suspendAndLog(value: T, c: Continuation<T>) {
        result += "suspend($value);"
        c.resume(value)
    }

    operator fun handleResult(value: String, c: Continuation<Nothing>) {
        result += "return($value);"
    }
}

fun builder(coroutine c: Controller.() -> Continuation<Unit>): String {
    val controller = Controller()
    c(controller).resume(Unit)
    return controller.result
}

fun <T> id(value: T) = value

fun box(): String {
    val value = builder {
        try {
            if (id(23) == 23) {
                return@builder suspendAndLog("OK")
            }
        }
        finally {
            result += "finally;"
        }
        result += "afterFinally;"
        "shouldNotReach"
    }
    if (value != "suspend(OK);finally;return(OK);") return "fail: $value"

    return "OK"
}