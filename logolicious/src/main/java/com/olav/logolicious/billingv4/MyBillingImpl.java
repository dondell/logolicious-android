package com.olav.logolicious.billingv4;

import static com.olav.logolicious.util.LogoliciousApp.showMessageOK;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.gson.Gson;
import com.olav.logolicious.BuildConfig;
import com.olav.logolicious.billingv3.Constants;
import com.olav.logolicious.screens.activities.ActivityMainEditor;
import com.olav.logolicious.util.LogoliciousApp;
import com.olav.logolicious.util.SubscriptionUtil.AppStatitics;

import java.util.ArrayList;
import java.util.List;

public class MyBillingImpl implements PurchasesUpdatedListener {

    private String TAG = MyBillingImpl.class.getSimpleName();
    private Activity context;
    public BillingClient billingClient;
    private List<Purchase> mPurchasesList;
    private List<ProductDetails> mSkuDetailsList = new ArrayList<>();

    public void initialize(ActivityMainEditor context) {
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(billingClientStateListener);
    }

    public MyBillingImpl(ActivityMainEditor context, List<Purchase> purchasesList) {
        this.context = context;
        this.mPurchasesList = purchasesList;
        initialize(context);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        mPurchasesList = purchases;
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private final BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                querySKUDetails();
                queryPurchases();
            }
        }

        @Override
        public void onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            if (billingClient.isReady()) {
                billingClient.startConnection(billingClientStateListener);
            }
        }
    };

    private void handlePurchase(Purchase purchase) {
        //This is for subscription/non-consumable
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    private final AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            AppStatitics.sharedPreferenceSet(context, "isSubscribed", 1);
            if (null != LogoliciousApp.subsDialog) {
                LogoliciousApp.subsDialog.cancel();
            }
            Toast.makeText(context, "onAcknowledgePurchaseResponse Successfully subscribed", Toast.LENGTH_SHORT).show();
        }
    };

    private void querySKUDetails() {
        List<QueryProductDetailsParams.Product> skuList = new ArrayList<>();
        skuList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.COM_OLAV_LOGOLICIOUS_SUBSCRIPTION)
                .setProductType(BillingClient.ProductType.SUBS).build());
        skuList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.ADDYOURLOGOAPP_2022)
                .setProductType(BillingClient.ProductType.SUBS).build());
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        QueryProductDetailsParams params1 = QueryProductDetailsParams.newBuilder().setProductList(skuList).build();
        billingClient.queryProductDetailsAsync(params1, (billingResult, list) -> {
            if (!list.isEmpty()) {
                mSkuDetailsList.clear();
                ActivityMainEditor.skuDetailsList.clear();
                mSkuDetailsList.addAll(list);
                ActivityMainEditor.skuDetailsList.addAll(list);
            }
        });
    }

    public void queryPurchases() {
        if (!billingClient.isReady()) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready");
        }
        Log.d(TAG, "queryPurchases: SUBS");
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build();
        billingClient.queryPurchasesAsync(params, (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {
                processPurchases(null);
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                processPurchases(list);

                // Clear first the flag
                AppStatitics.sharedPreferenceSet(context, "oldSubscriber", 0);

                // Check purchase history
                for (Purchase record : list) {
                    Log.i(TAG, "Purchased history item " + new Gson().toJson(record));
                    if (record.getSkus().contains(Constants.COM_OLAV_LOGOLICIOUS_SUBSCRIPTION)) {
                        // Has purchase the old Subscription
                        AppStatitics.sharedPreferenceSet(context, "oldSubscriber", 1);
                    }
                }
            }
        });

    }

    /**
     * Send purchase SingleLiveEvent and update purchases LiveData.
     * <p>
     * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
     * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
     */
    private void processPurchases(List<Purchase> purchasesList) {
        if (purchasesList != null) {
            Log.d(TAG, "processPurchases: " + purchasesList.size() + " purchase(s)");
        } else {
            Log.d(TAG, "processPurchases: with no purchases");
        }

        if (purchasesList != null) {
            if (purchasesList.size() == 0) {
                AppStatitics.sharedPreferenceSet(context, "isSubscribed", 0);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "Not subscribed " + purchasesList.size(), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "Has subscribed " + purchasesList.size(), Toast.LENGTH_SHORT).show();
                }
            }

            for (Purchase purchase : purchasesList) {
                mPurchasesList.add(purchase);
                ArrayList<String> sku = purchase.getSkus();
                String purchaseToken = purchase.getPurchaseToken();
                if (sku.contains(Constants.COM_OLAV_LOGOLICIOUS_SUBSCRIPTION) || sku.contains(Constants.ADDYOURLOGOAPP_2022)) {
                    //Only show subscription restored popup when isSubscribed key returned 0
                    if (AppStatitics.sharedPreferenceGet(context, "isSubscribed", 0) == 0) {
                        showMessageOK(context,
                                "Update successful. Your subscription is restored!",
                                (dialogInterface, i) -> dialogInterface.dismiss());
                    }

                    AppStatitics.sharedPreferenceSet(context, "isSubscribed", 1);
                    if (null != LogoliciousApp.subsDialog) {
                        LogoliciousApp.subsDialog.cancel();
                    }
                    break;
                }
                Log.d(TAG, "Register purchase with sku: " + sku + ", token: " + purchaseToken);
            }
        } else {
            AppStatitics.sharedPreferenceSet(context, "isSubscribed", 0);
        }
    }
}
