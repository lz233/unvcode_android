package moe.lz233.unvcode.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import moe.lz233.unvcode.R
import moe.lz233.unvcode.dao.BaseDao
import moe.lz233.unvcode.databinding.ActivityMainBinding
import moe.lz233.unvcode.util.LogUtil
import moe.lz233.unvcode.util.ktx.unvcode

class MainActivity : AppCompatActivity() {

    private val viewBuilding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBuilding.root)
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (mode == Configuration.UI_MODE_NIGHT_NO) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        viewBuilding.inputCardView.setOnClickListener {
            viewBuilding.inputEditText.requestFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(viewBuilding.inputEditText, 0)
        }
        viewBuilding.skipAsciiChip.setOnCheckedChangeListener { _, b ->
            BaseDao.skipAscii = b
        }
        viewBuilding.HighMseChip.setOnCheckedChangeListener { _, b ->
            if (b)
                BaseDao.mse = 0.5f
            else
                BaseDao.mse = 0.1f
        }
        viewBuilding.convertChip.setOnClickListener {
            if (viewBuilding.inputEditText.text.toString() == "") {
                viewBuilding.outputTextView.text = getString(R.string.noText)
            } else {
                val result = viewBuilding.inputEditText.text.toString().unvcode()
                text = result.first
                viewBuilding.outputTextView.text = "${result.first}\n${result.second}"
            }
        }
        viewBuilding.outputCardView.setOnClickListener {
            if (text == "") {
                LogUtil.toast(getString(R.string.noText))
            } else {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Unvcode", text))
            }
        }
    }
}