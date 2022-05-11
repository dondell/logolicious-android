package com.olav.logolicious.screens.fragments

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.olav.logolicious.R

class TipFontFeature : DialogFragment() {
    var v: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        v = inflater.inflate(layResId, container, false)
        val gotIt = v?.findViewById<Button>(R.id.gotIt)
        val textView = v?.findViewById<TextView>(R.id.textView)
        gotIt?.setOnClickListener { dismiss() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView?.text =
                Html.fromHtml(getString(R.string.font_feature_tip), Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView?.text = Html.fromHtml(getString(R.string.font_feature_tip))
        }
        return v
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        var layResId = 0
        var fragNum = 0
        @JvmStatic
        fun newInstance(text: String?, fragNum: Int, layResId: Int): TipFontFeature {
            Companion.layResId = layResId
            Companion.fragNum = fragNum
            val f = TipFontFeature()
            val b = Bundle()
            b.putString("msg", text)
            f.arguments = b
            return f
        }
    }
}