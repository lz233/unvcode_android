package moe.lz233.unvcode.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.google.code.appengine.awt.Color
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

    fun 真画皮(字: Char):Bitmap{
        val bitmap = Bitmap.createBitmap(100,100,Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.BLACK)
        canvas.drawText(字.toString(),0f,90f, Paint().apply {
            textSize=100f
            typeface= Typeface.DEFAULT
            color=android.graphics.Color.WHITE
        })
        return bitmap
    }

    private fun 画皮(字: Char): List<Int> {
        val bitmap = Bitmap.createBitmap(100,100,Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.BLACK)
        canvas.drawText(字.toString(),0f,90f, Paint().apply {
            textSize=100f
            typeface= Typeface.DEFAULT
            color=android.graphics.Color.WHITE
        })
        val pix=IntArray(100*100)
        bitmap.getPixels(pix,0,100,0,0,100,100)
        return mutableListOf<Int>().apply {
            pix.forEach {
                val color = Color(it)
                add(color.red / 255)
                add(color.green / 255)
                add(color.blue / 255)
            }
        }
    }

    private fun 比较(字1: Char, 字2: Char) = (画皮(字1) minus 画皮(字2)).variance()

    private fun 假面(字: Char, skipAscii: Boolean, mse: Double = 0.1): Pair<Double, Char> {
        if ((字.code < 128) and skipAscii) return (-1.0 to 字)
        val 候选组 = d[字] ?: return (-1.0 to 字)
        val 差异组 = mutableListOf<Double>().apply {
            候选组.forEach { add(比较(字, it)) }
        }
        val 差异 = 差异组.minOrNull()!!
        val 新字 = 候选组[差异组.minIndex()]
        if (差异 > mse) return (-1.0 to 字) else return (差异 to 新字)
    }

    fun 转换(s: String, skipAscii: Boolean = true, mse: Double = 0.1): Pair<String, List<Double>> {
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

fun String.unvcode(skipAscii: Boolean = true, mse: Double = 0.1) = Unvcode.转换(this, skipAscii, mse)