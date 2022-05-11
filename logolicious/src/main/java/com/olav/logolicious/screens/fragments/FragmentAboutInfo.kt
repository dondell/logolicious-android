package com.olav.logolicious.screens.fragments

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.olav.logolicious.R
import com.olav.logolicious.util.GlobalClass

class FragmentAboutInfo : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.about_info_credits, container, false)
        setHasOptionsMenu(false)
        val version = rootView.findViewById<View>(R.id.version) as TextView
        val showTipsNextime = rootView.findViewById<View>(R.id.showTipNextime) as CheckBox
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
            version.text = "Version " + packageInfo.versionName
        } catch (e2: PackageManager.NameNotFoundException) {
            version.text = "Version is up to date"
        }
        showTipsNextime.isChecked = GlobalClass.sqLiteHelper.isShowHint == 1
        showTipsNextime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                GlobalClass.sqLiteHelper.updateHint(1, 1)
            } else {
                GlobalClass.sqLiteHelper.updateHint(1, 0)
            }
        }
        return rootView
    }
}