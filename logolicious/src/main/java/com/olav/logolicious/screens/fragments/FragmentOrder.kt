package com.olav.logolicious.screens.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.olav.logolicious.R
import com.olav.logolicious.customize.adapters.AdapterGridLogos
import com.olav.logolicious.customize.adapters.ArrayHolderLogos
import com.olav.logolicious.screens.activities.ActivityMainEditor
import com.olav.logolicious.util.LogoliciousApp
import java.util.*

class FragmentOrder : Fragment() {
    private val arrayDesigns = ArrayList<ArrayHolderLogos>()
    private var adp: AdapterGridLogos? = null
    private var email: TextView? = null
    private var cb: CheckBox? = null
    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.about_orderpage, container, false)
        setHasOptionsMenu(false)
        preferences = ActivityMainEditor.act.getPreferences(Context.MODE_PRIVATE)
        editor = preferences?.edit()
        arrayDesigns.clear()
        adp = AdapterGridLogos(context, arrayDesigns, R.layout.grid_designedlogoitem)
        val bundle = this.arguments
        if (bundle != null) {
            val hideTransPixel = bundle.getBoolean("hideTransPixel", true)
            LogoliciousApp.setViewVisibilityOrGone(
                rootView,
                R.id.transparentPixel,
                !hideTransPixel
            )
        }
        email = rootView.findViewById<View>(R.id.email) as TextView
        email!!.setOnClickListener { //3rd solution
            val targetShareIntents: MutableList<Intent> = ArrayList()
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/html"
            val resInfos = activity!!.packageManager.queryIntentActivities(shareIntent, 0)
            if (!resInfos.isEmpty()) {
                println("Have package")
                for (resInfo in resInfos) {
                    val packageName = resInfo.activityInfo.packageName
                    Log.i("Package Name", packageName)
                    if (packageName.contains("com.google.android.gm") ||
                        packageName.contains("com.appple.app.email") ||
                        packageName.contains("com.trtf.blue") ||
                        packageName.contains("com.microsoft.office.outlook") ||
                        packageName.contains("me.bluemail.mail") ||
                        packageName.contains("com.yahoo.mobile.client.android.mail") ||
                        packageName.contains("com.fsck.k9") ||
                        packageName.contains("com.my.mail") ||
                        packageName.contains("com.boxer.email")
                    ) {
                        val r = Random()
                        val rOrderNumber = r.nextInt(999999 - 100000) + 1000000
                        val eIntent = Intent(Intent.ACTION_SEND)
                        eIntent.component = ComponentName(packageName, resInfo.activityInfo.name)
                        eIntent.putExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf(getString(R.string.OrderEmail))
                        )
                        eIntent.putExtra(
                            Intent.EXTRA_SUBJECT,
                            String.format(Locale.US, "[Order %d] Create my logo", rOrderNumber)
                        )
                        eIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            Html.fromHtml(getString(R.string.label_oderbodymsg))
                        )
                        eIntent.type = "text/html"
                        eIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        eIntent.setPackage(packageName)
                        targetShareIntents.add(eIntent)
                    }
                }
                if (targetShareIntents.isNotEmpty()) {
                    println("Have Intent")
                    val chooserIntent =
                        Intent.createChooser(targetShareIntents.removeAt(0), "Choose Email App")
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        targetShareIntents.toTypedArray()
                    )
                    startActivity(chooserIntent)
                } else {
                    LogoliciousApp.toast(activity, "No email client App", Toast.LENGTH_LONG)
                }
            }
        }
        email!!.text = Html.fromHtml(getString(R.string.ClickHereToOrder))
        cb = rootView.findViewById<View>(R.id.checkBoxNoLogoButton) as CheckBox
        if (null != cb) {
            cb!!.isChecked = preferences?.getBoolean("HideNologoButton", false) == true
            cb!!.setOnCheckedChangeListener { cb, flag ->
                if (flag) {
                    editor?.putBoolean("HideNologoButton", true)
                    editor?.commit()
                    LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.noLogo, false)
                    LogoliciousApp.setViewVisibility(
                        ActivityMainEditor.act,
                        R.id.spacerNoLogo,
                        false
                    )
                } else {
                    editor?.putBoolean("HideNologoButton", false)
                    editor?.commit()
                    LogoliciousApp.setViewVisibility(ActivityMainEditor.act, R.id.noLogo, true)
                    LogoliciousApp.setViewVisibility(
                        ActivityMainEditor.act,
                        R.id.spacerNoLogo,
                        true
                    )
                }
            }
        }
        return rootView
    }
}