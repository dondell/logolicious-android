package com.olav.logolicious.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dondell A. Batac on 9/6/2017.
 */

public class BillingService {

    public static String devPayLoad = "";
    //RSA Public Key
    public static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArX7hzkeSbcMrIGucOXQ9PW+gJZBNhVrA8HcZPzrr8nLuH8De+WgsSmlcsP2yZrYjcpd+fRzsWtvjK5QpoOpF7+EULZo7c6y09APS2BoTFssZ5Sfxb7MWpbcRpJxbrh9aLH2Z4JJ8gCA4+G5yiFt7+s2jIOwsCHe7iqPrDRRwq55RmXBwuOrg1mASCHKrWS4MPE1WY4iwwV92QdCwqy/ocz2awoEcjK9n/nEvrOl9e057ggB/eBzFzDQ4eRf8N9qLeECRzPQLKyajGKKn4zeaPyc775MPadgqiOtuY7wpioSL/sVvQGpJPgZHpp48aXnGpXRpLFvp4kjlpvbpNiEJHwIDAQAB";

    public IInAppBillingService mService;

    public ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    public static final int RESULT_CODE_PURCHASE = 1001;

    public void bindBillingService(Activity context){
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void unbindBillingService(Activity context){
        if (mService != null) {
            context.unbindService(mServiceConn);
        }
    }

    //Implementing subscriptions
    //Launching a purchase flow for a subscription is similar to launching the purchase flow for a product, with the exception that the product type must be set to "subs". The purchase result is delivered to your Activity's onActivityResult method, exactly as in the case of in-app products.

    public void subscribe(Activity context, String MY_SKU, IInAppBillingService service){
        Bundle bundle = null;
        try {
            if(null == service)
                return;
            bundle = mService.getBuyIntent(3, "com.olav.logolicious", MY_SKU, "subs", devPayLoad);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = bundle.getParcelable("RESPONSE_BUY_INTENT");
        if (bundle.getInt("RESPONSE_CODE") == 0) { //BILLING_RESPONSE_RESULT_OK
            // Start purchase flow (this brings up the Google Play UI).
            // Result will be delivered through onActivityResult().
            try {
                context.startIntentSenderForResult(pendingIntent.getIntentSender(), RESULT_CODE_PURCHASE, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    public void getSubscription() {
        Bundle activeSubs = null;
        try {
            if(null == mService)
                return;

            activeSubs = mService.getPurchases(3, "com.olav.logolicious", "subs", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (null == activeSubs)
            return;

        int response = activeSubs.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> purchaseDataList = activeSubs.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> signatureList = activeSubs.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            //To improve performance, the In-app Billing service returns only up to 700 products that are owned by the user when getPurchase is first called. If the user owns a large number of products, Google Play includes a String token that is mapped to the key INAPP_CONTINUATION_TOKEN in the response Bundle to indicate that more products can be retrieved. Your application can then make a subsequent getPurchases call and pass in this token as an argument. Google Play continues to return a continuation token in the response Bundle until all of the products that are owned by the user are sent to your app.
            String continuationToken = activeSubs.getString("INAPP_CONTINUATION_TOKEN");

            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku = ownedSkus.get(i);

                // do something with this purchase information
                // e.g. display the updated list of products owned by user
            }

            // if continuationToken != null, call getPurchases again
            // and pass in the token to retrieve more items

        }
    }

    /**
     * Querying for items available for purchase
     * @param context
     */
    public void queryItemsForPurchase(Activity context){
        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add("premiumUpgrade");
        skuList.add("gas");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);


        Bundle skuDetails = null;
        try {
            skuDetails = mService.getSkuDetails(3, context.getPackageName(), "inapp", querySkus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        int response = skuDetails.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

            for (String thisResponse : responseList) {
                JSONObject object = null;
                String sku = null;
                String price = null;
                try {
                    object = new JSONObject(thisResponse);
                    sku = object.getString("productId");
                    price = object.getString("price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                if (sku.equals("premiumUpgrade"))
//                    mPremiumUpgradePrice = price;
//                else if (sku.equals("gas"))
//                    mGasPrice = price;
            }
        }
    }

    /**
     * Purchasing an item
     * @param context
     * @param packageName
     * @param sku
     */
    public void purchase(Activity context, String packageName, String sku){
        Bundle buyIntentBundle = null;
        try {
            buyIntentBundle = mService.getBuyIntent(3, packageName, sku, "inapp", devPayLoad);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(null == buyIntentBundle)
            return;

        //The next step is to extract a PendingIntent from the response Bundle with key BUY_INTENT, as shown here:
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        //To complete the purchase transaction, call the startIntentSenderForResult method and use the PendingIntent that you created. This example uses an arbitrary value of 1001 for the request code:
        try {
            context.startIntentSenderForResult(
                    pendingIntent.getIntentSender(),
                    RESULT_CODE_PURCHASE, new Intent(),
                    Integer.valueOf(0),
                    Integer.valueOf(0),
                    Integer.valueOf(0));
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Querying for purchased items
     * @param packageName
     *
     */
    public void queryPurchases(String packageName){
        Bundle ownedItems = null;
        try {
            ownedItems = mService.getPurchases(3, packageName, "inapp", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(null == ownedItems)
            return;

        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String>  signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            //To improve performance, the In-app Billing service returns only up to 700 products that are owned by the user when getPurchase is first called. If the user owns a large number of products, Google Play includes a String token that is mapped to the key INAPP_CONTINUATION_TOKEN in the response Bundle to indicate that more products can be retrieved. Your application can then make a subsequent getPurchases call and pass in this token as an argument. Google Play continues to return a continuation token in the response Bundle until all of the products that are owned by the user are sent to your app.
            String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku = ownedSkus.get(i);

                // do something with this purchase information
                // e.g. display the updated list of products owned by user
            }

            // if continuationToken != null, call getPurchases again
            // and pass in the token to retrieve more items
        }

    }

}