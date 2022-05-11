package com.olav.logolicious.screens.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.olav.logolicious.R
import com.olav.logolicious.screens.activities.AboutSaveSettings

class AboutUsFragment : DialogFragment() {
    private var mTabHost: FragmentTabHost? = null
    private var viewPager: ViewPager? = null
    private var adapter: AboutUsPagerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.about_dialog_fragment, container, false)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        mTabHost = view.findViewById<View>(R.id.tabs) as FragmentTabHost
        mTabHost!!.setup(activity!!, childFragmentManager)
        if (!onlyShowCurrentTab) {
            //mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("ABOUT"), Fragment_AboutInfo.class, null);
            mTabHost!!.addTab(
                mTabHost!!.newTabSpec("tab2").setIndicator("SETTINGS"),
                AboutSaveSettings::class.java,
                null
            )
            //mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("NO LOGO?"), Fragment_Order.class, null);
        } else {
            // set transparent background
            val d: Drawable = ColorDrawable(Color.TRANSPARENT)
            d.alpha = 130
            dialog!!.window!!.setBackgroundDrawable(d)
            mTabHost!!.addTab(
                mTabHost!!.newTabSpec("").setIndicator(""),
                FragmentOrder::class.java,
                null
            )
            mTabHost!!.tabWidget.getChildAt(0).layoutParams.height = 0
        }
        adapter = AboutUsPagerAdapter(childFragmentManager, arguments)
        adapter!!.setTitles(arrayOf("ABOUT", "SETTINGS", "ORDER"))
        viewPager = view.findViewById<View>(R.id.pager) as ViewPager
        viewPager!!.adapter = adapter
        viewPager!!.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(i: Int) {
                mTabHost!!.currentTab = i
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(arg0: Int) {
            }
        })
        mTabHost!!.setOnTabChangedListener {
            val i = mTabHost!!.currentTab
            viewPager!!.currentItem = i
        }
        viewPager!!.currentItem = tab_number
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
    }

    inner class AboutUsPagerAdapter : FragmentPagerAdapter {
        var bundle: Bundle? = null
        lateinit var titles: Array<String>

        constructor(fm: FragmentManager?) : super(fm!!) {
        }

        constructor(fm: FragmentManager?, bundle: Bundle?) : super(
            fm!!
        ) {
            this.bundle = bundle
        }

        override fun getItem(num: Int): Fragment {
            // TODO Auto-generated method stub
            var fragment: Fragment? = null
            var hideTransPixel = true
            if (onlyShowCurrentTab) {
                fragment = FragmentOrder()
                hideTransPixel = false
            } else {
                hideTransPixel = true
                if (num == 0) {
                    fragment = FragmentAboutInfo()
                } else if (num == 1) {
                    //fragment = new AboutSaveSettings();
                } else if (num == 2) {
                    fragment = FragmentOrder()
                }
            }
            val args = Bundle()
            args.putSerializable(
                "aboutus",
                bundle!!.getSerializable(if (num == 0) "pros" else "cons")
            )
            args.putBoolean("isClick", false)
            args.putBoolean("hideTransPixel", hideTransPixel)
            fragment!!.arguments = args
            return fragment
        }

        override fun getCount(): Int {
            // TODO Auto-generated method stub
            return tab_total
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        @JvmName("setTitles1")
        fun setTitles(titles: Array<String>) {
            this.titles = titles
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private var tab_number = 0
        private var tab_total = 1
        var isClick = false
        private var onlyShowCurrentTab = false
        fun newInstance(num: Int, tab: Int, showCurrentTab: Boolean): AboutUsFragment {
            val f = AboutUsFragment()
            // supply num input as an argument
            val args = Bundle()
            args.putInt("num", num)
            f.arguments = args
            tab_number = tab
            onlyShowCurrentTab = showCurrentTab
            if (onlyShowCurrentTab) {
                tab_total = 1
                tab_number = 0
            } else tab_total = 3
            return f
        }
    }
}