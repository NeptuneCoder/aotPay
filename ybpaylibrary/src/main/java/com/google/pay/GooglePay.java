package com.google.pay;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.yiba.pay.YiBaPayConfig;

import java.io.Serializable;
import java.util.List;


/**
 * Created by yh on 2018/1/17.
 * // IAB Helper error codes
 */

public class GooglePay {
    public static final int IABHELPER_ERROR_BASE = -1000;
    public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
    public static final int IABHELPER_BAD_RESPONSE = -1002;
    public static final int IABHELPER_VERIFICATION_FAILED = -1003;
    public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
    public static final int IABHELPER_USER_CANCELLED = -1005;
    public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int IABHELPER_MISSING_TOKEN = -1007;
    public static final int IABHELPER_UNKNOWN_ERROR = -1008;
    public static final int IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE = -1009;
    public static final int IABHELPER_INVALID_CONSUMPTION = -1010;
    public static final int IABHELPER_SUBSCRIPTION_UPDATE_NOT_AVAILABLE = -1011;
    public static final int ISECURITY_VERIFY_FAILE = -1012;
    public static final int BILLING_RESPONSE_RESULT_ERR = -1013;
    private String currBuyType = "";
    private IGooglePayStatus listener;
    private IabHelper mHelper = null;
    IabBroadcastReceiver mBroadcastReceiver;
    static final int RC_REQUEST = 10001;

    private Activity activity;
    private String base64;

    public GooglePay(final Activity activity, String base64, IGooglePayStatus listener) {
        this.listener = listener;
        GgPayInit(activity, base64);
    }


    public void GgPayInit(final Activity activity, String base64) {
        this.base64 = base64;
        this.activity = activity;
        mHelper = new IabHelper(activity, base64, new IabHelperCallback() {
            @Override
            public void onGgSuccess(OrderParam data) {
                listener.callBackStatus(data);
            }
        });

        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {

                    if (!result.isSuccess()) {
                        if (listener != null) {
                            listener.callBackStatus(IGooglePayResultListener.INIT_FAILED);
                        }
                        return;
                    }
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }

                    mBroadcastReceiver = new IabBroadcastReceiver(InitSuccessCallback);
                    IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    activity.registerReceiver(mBroadcastReceiver, broadcastFilter);
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                    }
                }

                @Override
                public void onPayStatus(int result) {
                    if (listener != null) {
                        listener.callBackStatus(result);
                    }
                }

                @Override
                public void onConsumedStatus(int result) {
                    if (listener != null) {
                        listener.callBackStatus(result);
                    }
                }
            });
        } catch (Exception e) {
            if (listener != null) {
                listener.callBackStatus(IGooglePayResultListener.INIT_SERVICE_FAILED);
            }
        }
    }


    //初始化google支付时需要的广播，目的是为了接受初始化成功后，去查询结果。
    private IabBroadcastReceiver.IabBroadcastListener InitSuccessCallback = new IabBroadcastReceiver.IabBroadcastListener() {

        @Override
        public void receivedBroadcast() {
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
            }
        }
    };

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            /**
             * 每当你购买消费品时，谷歌播放存储将不会被管理它的产品购买细节和其他东西在谷歌播放控制台。
             * 这就是为什么我们必须调用consumeAsync（）方法。当我们购买物品时，
             * Google Play商店保留记录物品一次性购买，并允许您第二次购买。
             */
            Log.i("test", "gasPurchase = " + result.isFailure());
            if (result.isFailure()) {
                return;
            }

            Purchase gasPurchase = inventory.getPurchase(productId);
            Log.i("test", "gasPurchase is null = " + (gasPurchase == null));
            if (gasPurchase != null) {
                try {
                    Log.i("test", "inventory = " + inventory.getPurchase(productId));
                    mHelper.consumeAsync(inventory.getPurchase(productId), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
                return;
            }


            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

        }
    };

    public void unRegister() {
        PackageManager pm = YiBaPayConfig.getContext().getPackageManager();
        Intent intent = new Intent(IabBroadcastReceiver.ACTION);
        List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
        if (list != null && !list.isEmpty()) {
            activity.unregisterReceiver(mBroadcastReceiver);
        }
    }

    private String productId = "vip_1_month";

    public void buyGoods(final Activity activity, final String productId, final String developerPayload) {
        this.productId = productId;
        this.developerpayload = developerPayload;
        this.currBuyType = IabHelper.ITEM_TYPE_INAPP;
        try {
            if (mHelper != null) {
                try {
                    mHelper.launchPurchaseFlow(activity, productId, 1001, mPurchaseFinishedListener, developerPayload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnLoginException e) {
            if (listener != null) {
                listener.callBackStatus(IGooglePayResultListener.UN_LOGIN);
            }
        }

    }

    public void launchSubscriptionPurchaseFlow(final Activity activity, final String productId, final String developerPayload) {
        this.productId = productId;
        this.currBuyType = IabHelper.ITEM_TYPE_SUBS;
        this.developerpayload = developerPayload;
        try {
            if (mHelper != null) {
                try {
                    mHelper.launchSubscriptionPurchaseFlow(activity, productId, 1001, mPurchaseFinishedListener, developerPayload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnLoginException e) {
            if (listener != null) {
                listener.callBackStatus(IGooglePayResultListener.UN_LOGIN);
            }
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                if (result.getResponse() == 7) {
                    Log.i("response:", "" + result.getResponse());
                }
                if (listener != null) {
                    listener.callBackStatus(result.getResponse());
                }
            }
            if (!verifyDeveloperPayload(purchase)) {
                //
                return;
            }
            if (currBuyType != null && currBuyType.equals(IabHelper.ITEM_TYPE_INAPP)) {
                Log.i("tag", "Purchase successful.");
                // bought 1/4 tank of gas. So consume it.
                if (purchase.getSku().equals(productId)) {//购买成功后，消耗商品
                    try {
                        mHelper.consumeAsync(purchase, mConsumeFinishedListener);//后面这个回调在源码中没有做事情，所以没有设置为null
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        if (listener != null) {
                            listener.callBackStatus(IGooglePayResultListener.NO_SPECIFIED_ITEM);
                        }
                    }
                    return;
                }
            }
        }
    };
    private String developerpayload = "";

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {

        return p != null && developerpayload != null && developerpayload.equals(p.getDeveloperPayload());

    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            Log.i("ConsumeFinished", " is  success = " + result.isSuccess());
            if (result.isSuccess()) {

                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                //TODO 消耗成功
            } else {

            }
        }
    };

    public static class OrderParam implements Serializable {
        public String purchaseData;
        public String dataSignature;
        public int responseCode;
        public int resultCode;
        public String currBuyType = ""; // 表示当前是内购还是订阅：// Item types
//        IabHelper
//        public static final String ITEM_TYPE_INAPP = "inapp";
//        public static final String ITEM_TYPE_SUBS = "subs";

        public boolean isInapp() {
            return currBuyType.equals(IabHelper.ITEM_TYPE_INAPP);
        }


        public boolean isSubs() {
            return currBuyType.equals(IabHelper.ITEM_TYPE_SUBS);
        }

        @Override
        public String toString() {
            return "{" +
                    "purchaseData:'" + purchaseData + '\'' +
                    ", dataSignature:'" + dataSignature + '\'' +
                    ", responseCode:" + responseCode +
                    ", resultCode=" + resultCode +
                    ", currBuyType:'" + currBuyType + '\'' +
                    '}';
        }

    }

    public boolean bindCallBack(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return false;


        return mHelper.handleActivityResult(requestCode, resultCode, data);
        // Pass on the activity result to the helper for handling

    }

    public void OnDispose() {
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
        }
//        mHelper = null;
    }

}
