package moe.lz233.unvcode.util.ktx

import moe.lz233.unvcode.dao.BaseDao
import moe.lz233.unvcode.util.Unvcode

fun String.unvcode(skipAscii: Boolean = BaseDao.skipAscii, mse: Float = BaseDao.mse) = Unvcode.转换(this, skipAscii, mse)