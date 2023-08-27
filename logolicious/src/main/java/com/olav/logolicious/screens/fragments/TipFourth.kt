package com.olav.logolicious.screens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.olav.logolicious.R
import com.olav.logolicious.screens.activities.Activity_Tip
import com.olav.logolicious.util.GlobalClass

class TipFourth : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(layResId, container, false)
        val btnOk = v?.findViewById<View>(R.id.tipOk) as ImageButton
        btnOk.setOnClickListener {
            if (fragNum == 4) requireActivity().finish() // last Tips fragment
            else (activity as Activity_Tip?)!!.pager!!.currentItem = fragNum
        }
        imageView1 = v?.findViewById<View>(R.id.imageView1) as? ImageView
        imageView1?.let {
            Glide.with(GlobalClass.getAppContext()).load(R.drawable.tip_4_guide).into(
                it
            )
        }
        return v
    }

    companion object {
        var layResId = 0
        var fragNum = 0
        var imageView1: ImageView? = null
        var v: View? = null
        fun newInstance(text: String?, fragNum: Int, layResId: Int, drawableId: Int): TipFourth {
            Companion.layResId = layResId
            Companion.fragNum = fragNum
            val f = TipFourth()
            val b = Bundle()
            b.putString("msg", text)
            b.putInt("drawable", drawableId)
            f.arguments = b
            return f
        }
    }
}