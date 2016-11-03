// TODO: muted automatically, investigate should it be ran for JS or not
// IGNORE_BACKEND: JS

// Auto-generated by org.jetbrains.kotlin.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
// WITH_RUNTIME

import java.util.ArrayList

import java.lang.Integer.MAX_VALUE as MaxI
import java.lang.Integer.MIN_VALUE as MinI
import java.lang.Byte.MAX_VALUE as MaxB
import java.lang.Byte.MIN_VALUE as MinB
import java.lang.Short.MAX_VALUE as MaxS
import java.lang.Short.MIN_VALUE as MinS
import java.lang.Long.MAX_VALUE as MaxL
import java.lang.Long.MIN_VALUE as MinL
import java.lang.Character.MAX_VALUE as MaxC
import java.lang.Character.MIN_VALUE as MinC

fun box(): String {
    val list1 = ArrayList<Int>()
    for (i in (MaxI - 2)..MaxI step 2) {
        list1.add(i)
        if (list1.size > 23) break
    }
    if (list1 != listOf<Int>(MaxI - 2, MaxI)) {
        return "Wrong elements for (MaxI - 2)..MaxI step 2: $list1"
    }

    val list2 = ArrayList<Int>()
    for (i in (MaxB - 2).toByte()..MaxB step 2) {
        list2.add(i)
        if (list2.size > 23) break
    }
    if (list2 != listOf<Int>((MaxB - 2).toInt(), MaxB.toInt())) {
        return "Wrong elements for (MaxB - 2).toByte()..MaxB step 2: $list2"
    }

    val list3 = ArrayList<Int>()
    for (i in (MaxS - 2).toShort()..MaxS step 2) {
        list3.add(i)
        if (list3.size > 23) break
    }
    if (list3 != listOf<Int>((MaxS - 2).toInt(), MaxS.toInt())) {
        return "Wrong elements for (MaxS - 2).toShort()..MaxS step 2: $list3"
    }

    val list4 = ArrayList<Long>()
    for (i in (MaxL - 2).toLong()..MaxL step 2) {
        list4.add(i)
        if (list4.size > 23) break
    }
    if (list4 != listOf<Long>((MaxL - 2).toLong(), MaxL)) {
        return "Wrong elements for (MaxL - 2).toLong()..MaxL step 2: $list4"
    }

    val list5 = ArrayList<Char>()
    for (i in (MaxC - 2)..MaxC step 2) {
        list5.add(i)
        if (list5.size > 23) break
    }
    if (list5 != listOf<Char>((MaxC - 2), MaxC)) {
        return "Wrong elements for (MaxC - 2)..MaxC step 2: $list5"
    }

    return "OK"
}
