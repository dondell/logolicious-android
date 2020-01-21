package com.olav.logolicious.util.SubscriptionUtil;

/**
 * Created by ASUS on 9/20/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by hrskrs on 5/5/2016.
 */
public class SubscriptionUtil {
    private static final int REQUEST_CODE = 10001;
    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArX7hzkeSbcMrIGucOXQ9PW+gJZBNhVrA8HcZPzrr8nLuH8De+WgsSmlcsP2yZrYjcpd+fRzsWtvjK5QpoOpF7+EULZo7c6y09APS2BoTFssZ5Sfxb7MWpbcRpJxbrh9aLH2Z4JJ8gCA4+G5yiFt7+s2jIOwsCHe7iqPrDRRwq55RmXBwuOrg1mASCHKrWS4MPE1WY4iwwV92QdCwqy/ocz2awoEcjK9n/nEvrOl9e057ggB/eBzFzDQ4eRf8N9qLeECRzPQLKyajGKKn4zeaPyc775MPadgqiOtuY7wpioSL/sVvQGpJPgZHpp48aXnGpXRpLFvp4kjlpvbpNiEJHwIDAQAB";
    public static final String TEST_SKU = "android.test.purchased";
    public static final String SUBSCRIPTION_SKU = "com.olav.logolicious.subscription";
    public static String SUBSCRIPTION_TOKEN = "";

    private IabHelper iabHelper;
    private Context context;

    private SubscriptionUtil() {
        //No instance
    }

    public SubscriptionUtil(Context context) {
        this.context = context;
        iabHelper = new IabHelper(context, base64EncodedPublicKey);
        iabHelper.enableDebugLogging(true, "TEST");
    }

    public boolean isUserSubscribed(Activity context){
        //check user already subscribed
        if(1 == AppStatitics.sharedPreferenceGet(context, "isSubscribed", 1)) {
            //no need to go further
            return true;
        }
        return false;
    }
}