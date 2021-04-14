package com.olav.logolicious.util.SubscriptionUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.LogoliciousApp;

import java.util.Map;

import static com.olav.logolicious.screens.activities.ActivityMainEditor.act;

/**
 * Created by Dondell A. Batac on 9/11/2017.
 */

public class AppStatitics {

    public static SubscriptionUtil mSubscriptionUtil;

    public static void initializeSaveCount(Activity context){
        if(-1 == AppStatitics.sharedPreferenceGet(context, "STAT_SAVE_SHARE_COUNT", -1)) {
            AppStatitics.sharedPreferenceSet(context, "STAT_SAVE_SHARE_COUNT", 0);
            AppStatitics.sharedPreferenceSet(act, "subscription_countdown", 0);
            AppStatitics.sharedPreferenceSet(act, "RATED", 0);
            Cursor cur = GlobalClass.sqLiteHelper.getSaveCount();
            try {
                while (cur.moveToNext()) {
                    int saveCount = cur.getInt(0);
                    int id = cur.getInt(1);
                    int isRated = cur.getInt(2);

                        GlobalClass.sqLiteHelper.updateSaveCount(id, 0, 0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (cur != null) {
                    cur.close();
                    cur = null;
                }
            }
        } else {
            int count = AppStatitics.sharedPreferenceGet(context, "STAT_SAVE_SHARE_COUNT", 0);
            Log.i("xxx","xxx STAT_SAVE_SHARE_COUNT " + count);
        }
    }

    public static void showSubscription(Activity context, int count){
        //Aside from 30 times usage(every other image), after more than 30 times usage,
        //show the subscription every other 3. e.g show subscription on 33, 36, 39.
        int nSkipper = ((count - 5) % 3);
        Log.i("xxx","xxx Subscription save/share=" + count);
        Log.i("xxx","xxx Subscription SUBSCRIPTION_DONE_SHOWING=" + LogoliciousApp.sharedPreferenceGet(GlobalClass.getAppContext(), "SUBSCRIPTION_DONE_SHOWING", -1));
        Log.i("xxx","xxx Subscription nSkipper=" + nSkipper);

        if(count == 5) { //first popup
            if(0 == LogoliciousApp.sharedPreferenceGet(GlobalClass.getAppContext(), "SUBSCRIPTION_DONE_SHOWING", -1)) {
                LogoliciousApp.sharedPreferenceSet(GlobalClass.getAppContext(), "SUBSCRIPTION_DONE_SHOWING", 1);
                LogoliciousApp.showSubscription(context, count);
            }
        } else {
            if(count > 6 && 0 == nSkipper) {
                LogoliciousApp.showSubscription(context, count);
            }
        }
    }

    public  static  void addSaveShareCount(Activity context){
        int count = AppStatitics.sharedPreferenceGet(context, "STAT_SAVE_SHARE_COUNT", -1);
        count = count + 1;
//        if(count < 61) { //limit only subscription showing to 61 is not good. Because our goal is to alert users for subscription.
            AppStatitics.sharedPreferenceSet(context, "STAT_SAVE_SHARE_COUNT", count);
            LogoliciousApp.sharedPreferenceSet(GlobalClass.getAppContext(), "SUBSCRIPTION_DONE_SHOWING", 0);
            Log.i("xxx", "xxx addSaveShareCount= " + AppStatitics.sharedPreferenceGet(context, "STAT_SAVE_SHARE_COUNT", -1));
//        }

        if (GlobalClass.subscriptionOkToShow && !LogoliciousApp.isLive || !LogoliciousApp.strIsNullOrEmpty(GlobalClass.picturePath)) {
            ActivityMainEditor.isShowSubscription(context);
        }
    }

    public static int getSaveShareCount(Activity context){
        int nRet = 0;
        nRet = AppStatitics.sharedPreferenceGet(context, "STAT_SAVE_SHARE_COUNT", -1);
        return nRet;
    }

    /*
         * For Application Shared Preferences
         *
         * */
    public static SharedPreferences sharedPreferenceGet(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static boolean sharedPreferenceExist(Context context, String strKey)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return false;
        return spf.contains(strKey);
    }
    public static int sharedPreferenceGet(Context context, String strKey, int nDefaultValue)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return nDefaultValue;

        int nRet = spf.getInt(strKey, nDefaultValue);
        return nRet;
    }
    public static String sharedPreferenceGet(Context context, String strKey, String strDefaultValue)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return strDefaultValue;

        String strRet = spf.getString(strKey, strDefaultValue);
        return strRet;
    }

    public static void sharedPreferenceSet(Context context, String strKey, int nValue)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return ;

        SharedPreferences.Editor ed = spf.edit();
        ed.putInt(strKey, nValue);
        ed.commit();
    }
    public static void sharedPreferenceSet(Context context, String strKey, String strValue)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return ;

        SharedPreferences.Editor ed = spf.edit();
        ed.putString(strKey, strValue);
        ed.commit();
    }
    public static void sharedPreferenceRemove(Context context, String strKey)
    {
        SharedPreferences spf = sharedPreferenceGet(context);
        if(null == spf)
            return ;
        if(!strKey.contains("%"))
        {
            SharedPreferences.Editor ed = spf.edit();
            ed.remove(strKey);
            ed.commit();
            return;
        }

        if(strKey.startsWith("%") || strKey.endsWith("%"))
        {
            //Msg_% or %Msg.., % should only be either BeginWith or EndWith
            String strItemKey = "";
            SharedPreferences.Editor ed = spf.edit();
            Map<String, ?> map = spf.getAll();
            for (Map.Entry<String, ?> entry : map.entrySet())
            {
                strItemKey = entry.getKey();
                if(strKey.startsWith("%"))
                {
                    if(strItemKey.endsWith(strKey.substring(1)))
                        ed.remove(strItemKey);
                }
                else if(strKey.endsWith("%"))
                {
                    if(strItemKey.startsWith(strKey.substring(0, strKey.length() - 1)))
                        ed.remove(strItemKey);
                }
            }
            ed.commit();
        }
    }

}
