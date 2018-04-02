package com.yiba.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.pay.IabBroadcastReceiver;
import com.google.pay.IabHelper;
import com.google.pay.IabResult;
import com.google.pay.Inventory;
import com.google.pay.Purchase;
import com.google.pay.Security;
import com.google.pay.UnLoginException;

import org.json.JSONException;

import java.util.ArrayList;
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
    private final Handler handler;

    //标记广播是否注册成功，用于反注册判断
    private boolean isRegisterStatus = false;
    private Activity activity;
    private String base64;

    public GooglePay(final Activity activity, String base64, Handler handler) {
        this.handler = handler;
        GgPayInit(activity, base64);
    }

    private IabHelper mHelper = null;
    IabBroadcastReceiver mBroadcastReceiver;

    public void GgPayInit(final Activity activity, String base64) {
        this.base64 = base64;
        this.activity = activity;
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with

        mHelper = new IabHelper(activity, base64);

        // enable debug logging (for a production application, you should set this to false).


        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        if (handler != null) {
                            Message msg = handler.obtainMessage();
                            msg.what = YiBaPayManager.GOOGLE_PAY;
                            msg.obj = IGooglePayResultListener.INIT_FAILED;
                            handler.sendMessage(msg);
                        }
                        return;
                    }
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }

                    // Important: Dynamically register for broadcast messages about updated purchases.
                    // We register the receiver here instead of as a <receiver> in the Manifest
                    // because we always call getPurchases() at startup, so therefore we can ignore
                    // any broadcasts sent while the app isn't running.
                    // Note: registering this listener in an Activity is a bad idea, but is done here
                    // because this is a SAMPLE. Regardless, the receiver must be registered after
                    // IabHelper is setup, but before first call to getPurchases().

                    mBroadcastReceiver = new IabBroadcastReceiver(InitSuccessCallback);
                    IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    activity.registerReceiver(mBroadcastReceiver, broadcastFilter);


                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
//                       complain("Error querying inventory. Another async operation in progress.");
                    }
                }

                @Override
                public void onPayStatus(int result) {
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = YiBaPayManager.GOOGLE_PAY;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onConsumedStatus(int result) {
                    if (handler != null) {
                        Message msg = handler.obtainMessage();
                        msg.what = YiBaPayManager.GOOGLE_PAY;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (Exception e) {
            if (handler != null) {
                Message msg = handler.obtainMessage();
                msg.what = YiBaPayManager.GOOGLE_PAY;
                msg.obj = IGooglePayResultListener.INIT_SERVICE_FAILED;
                handler.sendMessage(msg);
            }
        }
    }


    //初始化google支付时需要的广播，目的是为了接受初始化成功后，去查询结果。
    private IabBroadcastReceiver.IabBroadcastListener InitSuccessCallback = new IabBroadcastReceiver.IabBroadcastListener() {

        @Override
        public void receivedBroadcast() {
            isRegisterStatus = true;
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
            if (result.isSuccess()) {
                consumeAsyncProduct(result, inventory);
            }


            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

        }
    };

    public List<String> getGoodsName() {
        List<String> result = new ArrayList<>();
        result.add("vip_1_month");
        result.add("vip_3_month");
        result.add("vip_6_month");
        result.add("vip_12_month");

        result.add("svip_1_month");
        result.add("svip_3_month");
        result.add("svip_6_month");
        result.add("svip_12_month");
        return result;
    }

    public void consumeAsyncProduct(IabResult result, Inventory inventory) {
        boolean isHaveErr = false;
        for (String item : getGoodsName()) {
            Purchase gasPurchase1 = inventory.getPurchase(item);
            if (gasPurchase1 != null) {
                try {
                    mHelper.consumeAsync(gasPurchase1,
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();

                    isHaveErr = true;
                }
            }
        }
        if (handler != null && isHaveErr) {
            Message msg = handler.obtainMessage();
            msg.what = YiBaPayManager.GOOGLE_PAY;
            msg.obj = IGooglePayResultListener.CONFIRM_INVENTORY_FAILURE;
            handler.sendMessage(msg);
        }
    }

    public void unRegister() {
        if (isRegisterStatus) {
            PackageManager pm = YiBaPayConfig.getContext().getPackageManager();
            Intent intent = new Intent(IabBroadcastReceiver.ACTION);
            List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
            if (list != null && !list.isEmpty()) {
                activity.unregisterReceiver(mBroadcastReceiver);
            }
            isRegisterStatus = false;
        }
    }

    private String sku = "svip_6_month";

    public void buyGoods(final Activity activity, final String sku, final String packageName, final String developerPayload) {
        this.sku = sku;
        try {
            if (mHelper != null) {
                try {
                    mHelper.launchPurchaseFlow(activity, sku, 1001, mPurchaseFinishedListener, developerPayload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnLoginException e) {
            sendErrorMsg(IGooglePayResultListener.UN_LOGIN);
        }

    }

    private void sendErrorMsg(int obj) {
        if (handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = YiBaPayManager.GOOGLE_PAY;
            msg.obj = obj;
            handler.sendMessage(msg);
        }
    }


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                sendErrorMsg(result.getResponse());
                return;
            }
            // bought 1/4 tank of gas. So consume it.
            if (purchase.getSku().equals(sku)) {
                try {
                    Log.i("tag", "is  in there");
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);//后面这个回调在源码中没有做事情，所以没有设置为null
                } catch (IabHelper.IabAsyncInProgressException e) {
                    sendErrorMsg(IGooglePayResultListener.NO_SPECIFIED_ITEM);
                    return;
                }
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                //TODO 消耗成功
            } else {

            }
        }
    };

    public class OrderParam {
        String purchaseData;
        String dataSignature;
        int responseCode;
        int resultCode;
    }

    public boolean bindCallBack(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return false;
        Message msg = handler.obtainMessage();

        if (data != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                Purchase purchase = null;
                try {
                    purchase = new Purchase(sku, purchaseData, dataSignature);
                    String sku = purchase.getSku();

                    // Verify signature
                    if (!Security.verifyPurchase(base64, purchaseData, dataSignature)) {
//                        result = new IabResult(IABHELPER_VERIFICATION_FAILED, "Signature verification failed for sku " + sku);
//                        if (mPurchaseListener != null) mPurchaseListener.onIabPurchaseFinished(result, purchase);
                        return true;
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
//                    result = new IabResult(IABHELPER_BAD_RESPONSE, "Failed to parse purchase data.");
//                    if (mPurchaseListener != null) mPurchaseListener.onIabPurchaseFinished(result, null);
                    return true;
                }

//                if (mPurchaseListener != null) {
//                    mPurchaseListener.onIabPurchaseFinished(new IabResult(IabHelper.BILLING_RESPONSE_RESULT_OK, "Success"), purchase);
//                }

                OrderParam param = new OrderParam();
                param.purchaseData = purchaseData;
                param.dataSignature = dataSignature;
                param.purchaseData = purchaseData;
                param.responseCode = responseCode;
                param.resultCode = resultCode;
                msg.what = YiBaPayManager.GOOGLE_PAY;
                msg.obj = param;
                handler.sendMessage(msg);


            }
        }
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...

            return true;
        }
        return false;
    }

    public void OnDispose() {
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
        }
        mHelper = null;
    }

}
