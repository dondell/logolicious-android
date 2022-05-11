package com.olav.logolicious.screens.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.olav.logolicious.R
import com.olav.logolicious.screens.activities.Activity_Tip
import com.olav.logolicious.util.GlobalClass

class TipMain : Fragment() {
    var preferences: SharedPreferences? = null
    var isShowHint = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.tip_first, container, false)
        val imageView1 = v.findViewById<View>(R.id.imageView1) as ImageView
        Glide.with(GlobalClass.getAppContext()).load(R.drawable.tip_start).into(imageView1)
        val btnOk = v.findViewById<View>(R.id.buttonOk) as ImageButton
        btnOk.setOnClickListener { (activity as Activity_Tip?)!!.pager!!.currentItem = 1 }
        val buttonSkip = v.findViewById<View>(R.id.buttonSkip) as ImageButton
        buttonSkip.setOnClickListener { (activity as Activity_Tip?)!!.finish() }
        val checkBoxHint = v.findViewById<View>(R.id.showTipNextime) as CheckBox
        checkBoxHint.setOnCheckedChangeListener { buttonView, isChecked -> // TODO Auto-generated method stub
            if (isChecked) {
                GlobalClass.sqLiteHelper.updateHint(1, 0)
            } else {
                GlobalClass.sqLiteHelper.updateHint(1, 1)
            }
        }
        return v
    }

    fun skip(v: View?) {
        this.activity!!.finish()
    }

    companion object {
        fun newInstance(text: String?): TipMain {
            val f = TipMain()
            val b = Bundle()
            b.putString("msg", text)
            f.arguments = b
            return f
        }
    }
}