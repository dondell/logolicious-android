package com.olav.logolicious.screens.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.olav.logolicious.R
import com.olav.logolicious.screens.fragments.TipFourth
import com.olav.logolicious.screens.fragments.TipMain
import com.olav.logolicious.screens.fragments.TipSecond
import com.olav.logolicious.screens.fragments.TipThird

class Activity_Tip : FragmentActivity() {
    @JvmField
    var pager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tips)
        pager = findViewById<View>(R.id.viewPager) as ViewPager
        pager!!.adapter = MyPagerAdapter(supportFragmentManager)
    }

    private inner class MyPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        override fun getItem(pos: Int): Fragment {
            return when (pos) {
                0 -> TipMain.newInstance("FirstFragment, Instance 1")
                1 -> TipSecond.newInstance(
                    "SecondFragment, Instance 2",
                    2,
                    R.layout.tip_second,
                    R.drawable.tip_step1_2
                )
                2 -> TipThird.newInstance(
                    "ThirdFragment, Instance 3",
                    3,
                    R.layout.tip_third,
                    R.drawable.tip_step3_4
                )
                3 -> TipFourth.newInstance(
                    "FourthFragment, Instance 4",
                    4,
                    R.layout.tip_fourth,
                    R.drawable.tip_4_guide
                )
                else -> TipMain.newInstance("FirstFragment, Default")
            }
        }

        override fun getCount(): Int {
            return 4
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            false
        } else super.onKeyDown(keyCode, event)
    }
}