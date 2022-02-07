package moe.lz233.unvcode.util

import kotlin.math.pow

fun List<Int>.sum(): Int {
    var sum = 0
    this.forEach { sum += it }
    return sum
}

fun List<Int>.average(): Double = this.sum().toDouble() / this.size

fun List<Int>.variance(): Double {
    val average = this.average()
    var variance = 0.0
    this.forEach {
        variance += ((it - average).pow(2.0))
    }
    return variance / this.size
}

infix fun List<Int>.minus(other: List<Int>): List<Int> {
    if (this.size != other.size) throw Throwable("?!")
    return mutableListOf<Int>().apply {
        for (i in 0..this@minus.lastIndex) {
            add(this@minus[i] - other[i])
        }
    }
}

fun List<Double>.minIndex(): Int {
    var minIndex = 0
    for (i in 1..this.lastIndex) {
        if (this[i] < this[minIndex])minIndex=i
    }
    return minIndex
}