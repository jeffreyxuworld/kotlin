package foo

class Test() {
    var a = 0
        get() = field * 1

    inline var b: Int
        get() = a * 3
        set(c: Int) {
            a = c - 1
        }

    val d = 44
}

val Test.d: Int get() = 55

//fun Test.d(): Int = 17 + b

fun box(): String {
    val c = Test()
    if (c.a != 0) return "fail1: ${c.a}";
    if (c.b != 0) return "fail2: ${c.b}";
    c.a = 3;
    if (c.b != 9) return "fail3: ${c.a}";
    c.b = 10;
    if (c.a != 9) return "fail4: ${c.a}";
    if (c.b != 27) return "fail5: ${c.b}";
    if (c.d != 44) return "fail6: ${c.d}";
    return "OK";
}