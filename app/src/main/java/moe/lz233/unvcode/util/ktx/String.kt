package moe.lz233.unvcode.util.ktx

import moe.lz233.unvcode.util.Unvcode

fun String.unvcode(skipAscii: Boolean = true, mse: Double = 0.1) = Unvcode.转换(this, skipAscii, mse)