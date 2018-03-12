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
import com.google.pay.UnLoginException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yh on 2018/1/17.
 *  // IAB Helper error codes

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

    public  GooglePay(final Activity activity, String base64,Handler handler){
        this.handler = handler;
        GgPayInit(activity,base64);
    }
    private IabHelper mHelper = null;
    IabBroadcastReceiver mBroadcastReceiver;

    public void GgPayInit(final Activity activity, String base64){
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
                       if (handler!=null){
                           Message msg = handler.obtainMessage();
                           msg.what =YiBaPayManager.GOOGLE_PAY;
                           msg.obj = IGooglePayResultListener.INIT_FAILED;
                           handler.sendMessage(msg);
                       }

                       //TODO 错误提示
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

               }
           });
       }catch (Exception e){
           if (handler!=null){
               Message msg = handler.obtainMessage();
               msg.what =YiBaPayManager.GOOGLE_PAY;
               msg.obj = IGooglePayResultListener.INIT_SERVICE_FAILED;
               handler.sendMessage(msg);
           }
       }
    }


    //初始化google支付时需要的广播，目的是为了接受初始化成功后，去查询结果。
    private IabBroadcastReceiver.IabBroadcastListener InitSuccessCallback = new IabBroadcastReceiver.IabBroadcastListener(){

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
//            Purchase gasPurchase0 = inventory.getPurchase("svip_6_month");
//            Purchase gasPurchase1= inventory.getPurchase("svip_3_month");
//            Purchase gasPurchase2 = inventory.getPurchase("svip_1_month");
//            Purchase gasPurchase3 = inventory.getPurchase("svip_12_month");
            if (result.isSuccess()){
                consumeAsyncProduct(result,inventory);
            }

//            Log.i("gasPurchase",String.valueOf(inventory == null));
//            Log.i("gasPurchase",String.valueOf(result == null));
//            if (gasPurchase1 != null ) {
//                try {
//                    mHelper.consumeAsync(gasPurchase1,
//                            null);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    e.printStackTrace();
//                    if (handler!=null){
//                        Message msg = handler.obtainMessage();
//                        msg.what =YiBaPayManager.GOOGLE_PAY;
//                        msg.obj = IGooglePayResultListener.CONFIRM_INVENTORY_FAILURE;
//                        handler.sendMessage(msg);
//                    }
//                }
//                return;
//            }

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

        }
    };

    public List<String >  getGoodsName(){
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

    public void consumeAsyncProduct(IabResult result, Inventory inventory){
        boolean isHaveErr = false;
        for (String item:getGoodsName()){
            Purchase gasPurchase1= inventory.getPurchase(item);
            if (gasPurchase1 != null ) {
                try {
                    mHelper.consumeAsync(gasPurchase1,
                            null);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();

                    isHaveErr = true;
                }
            }
        }
        if (handler!=null && isHaveErr){
            Message msg = handler.obtainMessage();
            msg.what =YiBaPayManager.GOOGLE_PAY;
            msg.obj = IGooglePayResultListener.CONFIRM_INVENTORY_FAILURE;
            handler.sendMessage(msg);
        }
    }
    public void unRegister(){
            if (isRegisterStatus){
                PackageManager pm = YiBaPayConfig.getContext().getPackageManager();
                Intent intent = new Intent(IabBroadcastReceiver.ACTION);
                List<ResolveInfo> list =  pm.queryBroadcastReceivers(intent,0);
                if (list!=null && !list.isEmpty()){
                    activity.unregisterReceiver(mBroadcastReceiver);
                }
                isRegisterStatus = false;
            }
    }
    private String sku = "svip_6_month";
    public void buyGoods(final Activity activity,final String sku,final String packageName,final String developerPayload) {
        this.sku = sku;
        try {
            if (mHelper !=null){
//                mHelper.buyGoods(activity,sku,packageName,developerPayload);
                try {
                    mHelper.launchPurchaseFlow(activity,sku,1001,mPurchaseFinishedListener,developerPayload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }catch (UnLoginException e){
            sendErrorMsg(IGooglePayResultListener.UN_LOGIN);
        }

    }
    private void sendErrorMsg(int obj){
        if (handler!=null){
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
                    Log.i("tag","is  in there");
                    mHelper.consumeAsync(purchase, null);//后面这个回调在源码中没有做事情，所以没有设置为null
                } catch (IabHelper.IabAsyncInProgressException e) {
                    sendErrorMsg(IGooglePayResultListener.UN_LOGIN);
                    return;
                }
            }


        }
    };
    public boolean bindCallBack(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return false;
        Message msg = handler.obtainMessage();

        if (requestCode == 1001) {
            if (data  !=null){
                if (resultCode == Activity.RESULT_OK) {
                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                    msg.what = YiBaPayManager.GOOGLE_PAY;
                    msg.obj =purchaseData;
                    handler.sendMessage(msg);
                }
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

    public void OnDispose(){
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
        }
        mHelper = null;
    }

}
