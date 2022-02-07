package moe.lz233.unvcode.dao

import moe.lz233.unvcode.App

object BaseDao {
    var skipAscii: Boolean
        get() = App.sp.getBoolean("skipAscii", true)
        set(value) = App.editor.putBoolean("skipAscii", value).apply()

    var mse: Float
        get() = App.sp.getFloat("mse", 0.1f)
        set(value) = App.editor.putFloat("mse", value).apply()
}