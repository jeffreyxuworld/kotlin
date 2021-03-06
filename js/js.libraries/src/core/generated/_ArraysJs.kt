@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("ArraysKt")

package kotlin.collections

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.comparisons.*
import java.util.*

/**
 * Returns a [List] that wraps the original array.
 */
public fun <T> Array<out T>.asList(): List<T> {
    return ArrayList<T>(this as Array<Any?>)
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun ByteArray.asList(): List<Byte> {
    return (this as Array<Byte>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun ShortArray.asList(): List<Short> {
    return (this as Array<Short>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun IntArray.asList(): List<Int> {
    return (this as Array<Int>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun LongArray.asList(): List<Long> {
    return (this as Array<Long>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun FloatArray.asList(): List<Float> {
    return (this as Array<Float>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun DoubleArray.asList(): List<Double> {
    return (this as Array<Double>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun BooleanArray.asList(): List<Boolean> {
    return (this as Array<Boolean>).asList()
}

/**
 * Returns a [List] that wraps the original array.
 */
public inline fun CharArray.asList(): List<Char> {
    return (this as Array<Char>).asList()
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun ByteArray.toTypedArray(): Array<Byte> {
    return copyOf() as Array<Byte>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun ShortArray.toTypedArray(): Array<Short> {
    return copyOf() as Array<Short>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun IntArray.toTypedArray(): Array<Int> {
    return copyOf() as Array<Int>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun LongArray.toTypedArray(): Array<Long> {
    return copyOf() as Array<Long>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun FloatArray.toTypedArray(): Array<Float> {
    return copyOf() as Array<Float>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun DoubleArray.toTypedArray(): Array<Double> {
    return copyOf() as Array<Double>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun BooleanArray.toTypedArray(): Array<Boolean> {
    return copyOf() as Array<Boolean>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun CharArray.toTypedArray(): Array<Char> {
    return copyOf() as Array<Char>
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Array<out T>.copyOfRange(fromIndex: Int, toIndex: Int): Array<T> {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ByteArray.copyOfRange(fromIndex: Int, toIndex: Int): ByteArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ShortArray.copyOfRange(fromIndex: Int, toIndex: Int): ShortArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun IntArray.copyOfRange(fromIndex: Int, toIndex: Int): IntArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun LongArray.copyOfRange(fromIndex: Int, toIndex: Int): LongArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun FloatArray.copyOfRange(fromIndex: Int, toIndex: Int): FloatArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun DoubleArray.copyOfRange(fromIndex: Int, toIndex: Int): DoubleArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun BooleanArray.copyOfRange(fromIndex: Int, toIndex: Int): BooleanArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharArray.copyOfRange(fromIndex: Int, toIndex: Int): CharArray {
    return this.asDynamic().slice(fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Array<out T>.copyOf(): Array<T> {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ByteArray.copyOf(): ByteArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ShortArray.copyOf(): ShortArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun IntArray.copyOf(): IntArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun LongArray.copyOf(): LongArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun FloatArray.copyOf(): FloatArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun DoubleArray.copyOf(): DoubleArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun BooleanArray.copyOf(): BooleanArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharArray.copyOf(): CharArray {
    return this.asDynamic().slice()
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ByteArray.copyOf(newSize: Int): ByteArray {
    return arrayCopyResize(this, newSize, 0)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ShortArray.copyOf(newSize: Int): ShortArray {
    return arrayCopyResize(this, newSize, 0)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun IntArray.copyOf(newSize: Int): IntArray {
    return arrayCopyResize(this, newSize, 0)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun LongArray.copyOf(newSize: Int): LongArray {
    return arrayCopyResize(this, newSize, 0L)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun FloatArray.copyOf(newSize: Int): FloatArray {
    return arrayCopyResize(this, newSize, 0.0f)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun DoubleArray.copyOf(newSize: Int): DoubleArray {
    return arrayCopyResize(this, newSize, 0.0)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun BooleanArray.copyOf(newSize: Int): BooleanArray {
    return arrayCopyResize(this, newSize, false)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun CharArray.copyOf(newSize: Int): CharArray {
    return arrayCopyResize(this, newSize, '\u0000')
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun <T> Array<out T>.copyOf(newSize: Int): Array<T?> {
    return arrayCopyResize(this, newSize, null)
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Array<out T>.plusElement(element: T): Array<T> {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun <T> Array<out T>.plus(element: T): Array<T> {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun ByteArray.plus(element: Byte): ByteArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun ShortArray.plus(element: Short): ShortArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun IntArray.plus(element: Int): IntArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun LongArray.plus(element: Long): LongArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun FloatArray.plus(element: Float): FloatArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun DoubleArray.plus(element: Double): DoubleArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun BooleanArray.plus(element: Boolean): BooleanArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun CharArray.plus(element: Char): CharArray {
    return this.asDynamic().concat(arrayOf(element))
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun <T> Array<out T>.plus(elements: Collection<T>): Array<T> {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun ByteArray.plus(elements: Collection<Byte>): ByteArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun ShortArray.plus(elements: Collection<Short>): ShortArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun IntArray.plus(elements: Collection<Int>): IntArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun LongArray.plus(elements: Collection<Long>): LongArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun FloatArray.plus(elements: Collection<Float>): FloatArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun DoubleArray.plus(elements: Collection<Double>): DoubleArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun BooleanArray.plus(elements: Collection<Boolean>): BooleanArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] collection.
 */
public operator fun CharArray.plus(elements: Collection<Char>): CharArray {
    return arrayPlusCollection(this, elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun <T> Array<out T>.plus(elements: Array<out T>): Array<T> {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun ByteArray.plus(elements: ByteArray): ByteArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun ShortArray.plus(elements: ShortArray): ShortArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun IntArray.plus(elements: IntArray): IntArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun LongArray.plus(elements: LongArray): LongArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun FloatArray.plus(elements: FloatArray): FloatArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun DoubleArray.plus(elements: DoubleArray): DoubleArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun BooleanArray.plus(elements: BooleanArray): BooleanArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [elements] array.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun CharArray.plus(elements: CharArray): CharArray {
    return this.asDynamic().concat(elements)
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun <T> Array<out T>.contentEquals(other: Array<out T>): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun ByteArray.contentEquals(other: ByteArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun ShortArray.contentEquals(other: ShortArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun IntArray.contentEquals(other: IntArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun LongArray.contentEquals(other: LongArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun FloatArray.contentEquals(other: FloatArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun DoubleArray.contentEquals(other: DoubleArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun BooleanArray.contentEquals(other: BooleanArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *structurally* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 */
@SinceKotlin("1.1")
@library("arrayEquals")
public infix fun CharArray.contentEquals(other: CharArray): Boolean {
    return noImpl
}

/**
 * Returns `true` if the two specified arrays are *deeply* equal to one another,
 * i.e. contain the same number of the same elements in the same order.
 * 
 * If two corresponding elements are nested arrays, they are also compared deeply.
 * If any of arrays contains itself on any nesting level the behavior is undefined.
 */
@SinceKotlin("1.1")
@library("arrayDeepEquals")
public infix fun <T> Array<out T>.contentDeepEquals(other: Array<out T>): Boolean {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun <T> Array<out T>.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun ByteArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun ShortArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun IntArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun LongArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun FloatArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun DoubleArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun BooleanArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of the specified array as if it is a [List].
 */
@SinceKotlin("1.1")
@library("arrayToString")
public fun CharArray.contentToString(): String {
    return noImpl
}

/**
 * Returns a string representation of the contents of this array as if it is a [List].
 * Nested arrays are treated as lists too.
 * 
 * If any of arrays contains itself on any nesting level that reference
 * is rendered as `"[...]"` to prevent recursion.
 */
@SinceKotlin("1.1")
@library("arrayDeepToString")
public fun <T> Array<out T>.contentDeepToString(): String {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun <T> Array<out T>.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun ByteArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun ShortArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun IntArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun LongArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun FloatArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun DoubleArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun BooleanArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 */
@SinceKotlin("1.1")
@library("arrayHashCode")
public fun CharArray.contentHashCode(): Int {
    return noImpl
}

/**
 * Returns a hash code based on the contents of this array as if it is [List].
 * Nested arrays are treated as lists too.
 * 
 * If any of arrays contains itself on any nesting level the behavior is undefined.
 */
@SinceKotlin("1.1")
@library("arrayDeepHashCode")
public fun <T> Array<out T>.contentDeepHashCode(): Int {
    return noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun <T> Array<out T>.sort(comparison: (T, T) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun ByteArray.sort(comparison: (Byte, Byte) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun ShortArray.sort(comparison: (Short, Short) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun IntArray.sort(comparison: (Int, Int) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun LongArray.sort(comparison: (Long, Long) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun FloatArray.sort(comparison: (Float, Float) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun DoubleArray.sort(comparison: (Double, Double) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparison] function.
 */
@native
public fun CharArray.sort(comparison: (Char, Char) -> Int): Unit {
    noImpl
}

/**
 * Sorts the array in-place according to the order specified by the given [comparator] object.
 */
public fun <T> Array<out T>.sortWith(comparator: Comparator<in T>): Unit {
    if (size > 1)
        sort { a, b -> comparator.compare(a, b) }
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun ByteArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun ShortArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun IntArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun FloatArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun DoubleArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
@library("primitiveArraySort")
public fun CharArray.sort(): Unit {
    noImpl
}

/**
 * Sorts the array in-place.
 */
public fun <T: Comparable<T>> Array<out T>.sort(): Unit {
    if (size > 1)
        sort { a: T, b: T -> a.compareTo(b) }
}

/**
 * Sorts the array in-place.
 */
public fun LongArray.sort(): Unit {
    if (size > 1)
        sort { a: Long, b: Long -> a.compareTo(b) }
}

