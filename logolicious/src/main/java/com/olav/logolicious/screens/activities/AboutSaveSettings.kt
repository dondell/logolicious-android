package com.olav.logolicious.screens.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.widget.*
import com.olav.logolicious.R
import com.olav.logolicious.util.GlobalClass
import com.olav.logolicious.util.LogoliciousApp

class AboutSaveSettings : Activity() {
    var type1: CheckBox? = null
    var type2: CheckBox? = null
    var type3: CheckBox? = null
    var fileName: EditText? = null
    var saveFileName: Button? = null
    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.about_saving_setting)
        //this.setFinishOnTouchOutside(false);
        preferences = ActivityMainEditor.act.getPreferences(MODE_PRIVATE)
        editor = preferences?.edit()
        val note = findViewById<View>(R.id.notes) as WebView
        note.setBackgroundColor(Color.TRANSPARENT)
        val text = ("<html><body style='text-align:justify;color: #fff;margin: 0px;padding: 0px;'>"
                + "Note: Leave empty for automatic file name<br>      (we never overwrite originals!)"
                + "</body></html>")
        note.loadDataWithBaseURL(null, text, "text/html", "utf-8", null)
        type1 = findViewById<View>(R.id.type1) as CheckBox
        type1!!.setOnClickListener {
            type1!!.isChecked = true
            type2!!.isChecked = false
            type3!!.isChecked = false
            LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_HR_PNG
            LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_HR_PNG
            editor?.putString("SavingType", LogoliciousApp.TYPE_HR_PNG)
            editor?.commit()
        }
        type2 = findViewById<View>(R.id.type2) as CheckBox
        type2!!.setOnClickListener {
            type1!!.isChecked = false
            type2!!.isChecked = true
            type3!!.isChecked = false
            LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_HQ
            editor?.putString("SavingType", LogoliciousApp.TYPE_JPG_HQ)
            editor?.commit()
        }
        type3 = findViewById<View>(R.id.type3) as CheckBox
        type3!!.setOnClickListener {
            type1!!.isChecked = false
            type2!!.isChecked = false
            type3!!.isChecked = true
            LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_L
            LogoliciousApp.SAVING_TYPE = LogoliciousApp.TYPE_JPG_L
            editor?.putString("SavingType", LogoliciousApp.TYPE_JPG_L)
            editor?.commit()
        }
        val type = preferences?.getString("SavingType", "JPG_HQ")
        LogoliciousApp.SAVING_TYPE = type
        if (type!! == LogoliciousApp.TYPE_HR_PNG) {
            type1!!.isChecked = true
            type2!!.isChecked = false
            type3!!.isChecked = false
        } else if (type == LogoliciousApp.TYPE_JPG_HQ) {
            type1!!.isChecked = false
            type2!!.isChecked = true
            type3!!.isChecked = false
        } else if (type == LogoliciousApp.TYPE_JPG_L) {
            type1!!.isChecked = false
            type2!!.isChecked = false
            type3!!.isChecked = true
        }
        val promptalways = findViewById<View>(R.id.promptalways) as CheckBox
        promptalways.isChecked = preferences?.getBoolean("dontAskMeAgain", true) == true
        promptalways.setOnCheckedChangeListener { _, flag ->
            if (flag) {
                editor?.putBoolean("dontAskMeAgain", true)
                editor?.commit()
            } else {
                editor?.putBoolean("dontAskMeAgain", false)
                editor?.commit()
            }
        }
        fileName = findViewById<View>(R.id.fileName) as EditText
        val sharedPref = ActivityMainEditor.act.getPreferences(MODE_PRIVATE)
        val fName = sharedPref.getString("ImageFilename", null)
        if (!LogoliciousApp.strIsNullOrEmpty(fName)) {
            fileName!!.setText(fName)
        } else fileName!!.setText("")
        saveFileName = findViewById<View>(R.id.save) as Button
        saveFileName!!.setOnClickListener {
            val sharedPref = ActivityMainEditor.act.getPreferences(MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("ImageFilename", fileName!!.text.toString().trim { it <= ' ' })
            editor.commit()
            LogoliciousApp.toast(
                ActivityMainEditor.act,
                "Filename successfully set!",
                Toast.LENGTH_LONG
            )
        }
        val version = findViewById<View>(R.id.version) as TextView
        val showTipsNextime = findViewById<View>(R.id.showTipNextime) as CheckBox
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
            version.text = "Version " + packageInfo.versionName
        } catch (e2: PackageManager.NameNotFoundException) {
            version.text = "Version is up to date"
        }
        if (GlobalClass.sqLiteHelper.isShowHint == 1) {
            showTipsNextime.isChecked = true
        } else {
            showTipsNextime.isChecked = false
        }
        showTipsNextime.setOnCheckedChangeListener { buttonView, isChecked -> // TODO Auto-generated method stub
            if (isChecked) {
                GlobalClass.sqLiteHelper.updateHint(1, 1)
            } else {
                GlobalClass.sqLiteHelper.updateHint(1, 0)
            }
        }
        val officialWebsiteLink = findViewById<View>(R.id.officialWebsiteLink) as TextView
        officialWebsiteLink.text = Html.fromHtml(getString(R.string.website))
        officialWebsiteLink.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse("https://www.addyourlogoapp.com")
                )
            )
        }
    }
}