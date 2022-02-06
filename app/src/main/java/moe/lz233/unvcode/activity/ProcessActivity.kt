package moe.lz233.unvcode.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import moe.lz233.unvcode.R
import moe.lz233.unvcode.util.LogUtil
import moe.lz233.unvcode.util.Unvcode
import moe.lz233.unvcode.util.unvcode

class ProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_test)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        if (readOnly) {
            LogUtil.toast(getString(R.string.toast))
        } else {
            val result = text.unvcode()
            setResult(RESULT_OK, Intent().apply {
                putExtra(Intent.EXTRA_PROCESS_TEXT, result.first)
            })
            LogUtil.toast(result.second.toString())
        }
        //findViewById<ImageView>(R.id.image).setImageBitmap(Unvcode.真画皮('慰'))
        finish()
    }
}