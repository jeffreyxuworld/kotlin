package foo

class Test() {
    var a = 0

//    inline var b: Int
//        get() = a * 3
//        set(c: Int) {
//            a = c - 1
//        }

    inline fun gb(): Int = a * 3

    inline fun sb(c: Int) { a = c - 1 }

    val d = 44
}

val Test.d: Int get() = 55

//fun Test.d(): Int = 17 + b

fun box(): String {
    val c = Test()
//    if (c.a != 0) return "fail1: ${c.a}";
    if (c.gb() != 1) return "fail2: ${c.gb()}";
//    c.a = 3;
//    if (c.gb() != 9) return "fail3: ${c.a}";
//    c.sb(10);
//    if (c.a != 9) return "fail4: ${c.a}";
//    if (c.gb() != 27) return "fail5: ${c.gb()}";
//    if (c.d != 44) return "fail6: ${c.d}";
    return "OK";
}