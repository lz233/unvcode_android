package moe.lz233.unvcode.util

import android.graphics.*
import moe.lz233.unvcode.util.ktx.blue
import moe.lz233.unvcode.util.ktx.green
import moe.lz233.unvcode.util.ktx.red
import java.text.Normalizer

object Unvcode {
    private val d = mutableMapOf<Char, MutableList<Char>>()

    init {
        for (i in 0 until 65536) {
            val 字 = i.toChar()
            val 新字 = Normalizer.normalize(字.toString(), Normalizer.Form.NFKC).toCharArray()[0]
            if (字 != 新字) {
                d.putIfAbsent(新字, mutableListOf())
                d[新字]?.add(字)
            }
        }
    }

    fun 真画皮(字: Char): Bitmap {
        val 图 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        val 布 = Canvas(图)
        布.drawColor(Color.BLACK)
        布.drawText(字.toString(), 0f, 90f, Paint().apply {
            textSize = 100f
            typeface = Typeface.DEFAULT
            color = Color.WHITE
        })
        return 图
    }

    private fun 画皮(字: Char): List<Int> {
        val 图 = 真画皮(字)
        val 像素 = IntArray(100 * 100)
        图.getPixels(像素, 0, 100, 0, 0, 100, 100)
        return mutableListOf<Int>().apply {
            像素.forEach { 颜色 ->
                add(颜色.red() / 255)
                add(颜色.green() / 255)
                add(颜色.blue() / 255)
            }
            图.recycle()
        }
    }

    private fun 比较(字1: Char, 字2: Char) = (画皮(字1) minus 画皮(字2)).variance()

    private fun 假面(字: Char, skipAscii: Boolean, mse: Float = 0.1f): Pair<Double, Char> {
        if ((字.code < 128) and skipAscii) return (-1.0 to 字)
        val 候选组 = d[字] ?: return (-1.0 to 字)
        val 差异组 = mutableListOf<Double>().apply {
            候选组.forEach { add(比较(字, it)) }
        }
        val 差异 = 差异组.minOrNull()!!
        val 新字 = 候选组[差异组.minIndex()]
        return if (差异 > mse) (-1.0 to 字) else (差异 to 新字)
    }

    fun 转换(s: String, skipAscii: Boolean = true, mse: Float = 0.1f): Pair<String, List<Double>> {
        val 差异列 = mutableListOf<Double>()
        val 串 = StringBuilder().apply {
            s.toCharArray().forEach {
                val 结果 = 假面(it, skipAscii, mse)
                差异列.add(结果.first)
                append(结果.second)
            }
        }.toString()
        return (串 to 差异列)
    }
}