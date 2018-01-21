package com.yiba.pay;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.google.pay.IabBroadcastReceiver;
import com.google.pay.IabHelper;
import com.google.pay.IabResult;
import com.google.pay.Inventory;
import com.google.pay.UnLoginException;



/**
 * Created by yh on 2018/1/17.
 */

public class GooglePay {

    private final Handler handler;
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
    }

    //初始化google支付时需要的广播，目的是为了接受初始化成功后，去查询结果。
    private IabBroadcastReceiver.IabBroadcastListener InitSuccessCallback = new IabBroadcastReceiver.IabBroadcastListener(){

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



            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

        }
    };
    public void unRegister(){
            if (activity!=null && mBroadcastReceiver!=null){
                activity.unregisterReceiver(mBroadcastReceiver);
            }
    }

    public void buyGoods(final Activity activity,final String sku,final String packageName,final String developerPayload){
        try {
            if (mHelper !=null){
//                mHelper.buyGoods(activity,sku,packageName,developerPayload);
                try {
                    mHelper.launchPurchaseFlow(activity,sku,1001,null,developerPayload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }catch (UnLoginException e){
            if (handler!=null){
                Message msg = handler.obtainMessage();
                msg.what = YiBaPayManager.GOOGLE_PAY;
                msg.obj = IGooglePayResultListener.UN_LOGIN;
                handler.sendMessage(msg);
            }

        }

    }
    public boolean bindCallBack(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return false;


        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == activity.RESULT_OK) {
               Message msg = handler.obtainMessage();
               msg.what = YiBaPayManager.GOOGLE_PAY;
               msg.obj =purchaseData;
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

    public void OnDispose(){
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
        }
        mHelper = null;
    }

}
