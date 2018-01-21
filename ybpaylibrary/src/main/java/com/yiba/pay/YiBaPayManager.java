package com.yiba.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;
import java.util.Map;



/**
 * Created by yh on 2017/10/23.
 */

public class YiBaPayManager {

    private static final int ALI_PAY = 001;
    private static final int WEIXIN_PAY = 002;
    public static final int GOOGLE_PAY = 003;
    private IAliOrderInfo aliOrderInfo;
    private IWxOrderInfo wxOrderInfo;
    private static IResultListener resultListener;
    private static IAliResultListener aliResultListener;
    private static IWxResultListener wxResultListener;
    private static IGooglePayResultListener googleResultListener;

    private static final String BROADCAST_PERMISSION_DISC = "com.yiba.permissions.YiBaPay";
    public static final String ACTION = "com.yiba.pay.wxResult";
    private IntentFilter filter;


    private static Handler handler = new Handler(YiBaPayConfig.getContext().getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ALI_PAY:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                       if (resultListener != null){
                           resultListener.onAliSuccess();

                       }
                       if (aliResultListener!=null){
                           aliResultListener.onAliSuccess();
                       }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (resultListener != null){
                            resultListener.onAliFailed(Integer.parseInt(resultStatus.trim()));
                        }
                        if (aliResultListener!=null){
                            aliResultListener.onAliFailed(Integer.parseInt(resultStatus.trim()));
                        }
                    }
                    break;
                case WEIXIN_PAY:
                    String code = (String) msg.obj;
                    if ("0".equals(code)){
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (resultListener != null){
                            resultListener.onWxSuccess();
                        }
                        if (wxResultListener!=null){
                            wxResultListener.onWxSuccess();
                        }
                    }else{
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (resultListener != null){
                            resultListener.onWxFailed(Integer.parseInt(code.trim()));
                        }
                        if (wxResultListener!=null){
                            wxResultListener.onWxFailed(Integer.parseInt(code.trim()));
                        }
                    }

                    break;
                case GOOGLE_PAY:
                    if (msg.obj instanceof Integer){
                        int  ggCode = (int) msg.obj;
                        if (ggCode == IGooglePayResultListener.INIT_FAILED){
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (resultListener != null){
                                resultListener.onGgFailed(IGooglePayResultListener.INIT_FAILED);
                            }
                            if (googleResultListener!=null){
                                googleResultListener.onGgFailed(IGooglePayResultListener.INIT_FAILED);
                            }
                        }else if (ggCode == IGooglePayResultListener.UN_LOGIN){
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (resultListener != null){
                                resultListener.onGgFailed(IGooglePayResultListener.UN_LOGIN);
                            }
                            if (googleResultListener!=null){
                                googleResultListener.onGgFailed(IGooglePayResultListener.UN_LOGIN);
                            }
                        }
                    } else if (msg.obj instanceof String){
                        String result = (String) msg.obj;
                        if (googleResultListener!=null){
                            googleResultListener.onGgSuccess(result);
                        }
                    }

                    break;
            }

        }
    };


    private YiBaPayManager(){
        //初始化googepay
    }
    public void initWxCallback(){
        filter = new IntentFilter();
        filter.addAction(ACTION);
        YiBaPayConfig.getContext().registerReceiver(wxPayResult,filter,BROADCAST_PERMISSION_DISC,null);
    }

    private static class SingleTonHolder{
        private final static YiBaPayManager INSTANCE = new YiBaPayManager();
    }

    public static YiBaPayManager getInstance(){
        return SingleTonHolder.INSTANCE;
    }

    /**
     * 设置订单信息的实现
     * @param orderInfo
     */
    public void setAliOrderInfo(IAliOrderInfo orderInfo){
        this.aliOrderInfo = orderInfo;
    }


    /**
     * 设置订单信息的实现
     * @param orderInfo
     */
    public void setWxOrderInfo(IWxOrderInfo orderInfo){
        this.wxOrderInfo = orderInfo;
    }
    /**
     * 设置支付结果的回调
     * @param resultListener
     */
    public void setOnResultListener(IResultListener resultListener){
        this.resultListener  = resultListener;
    }

    /**
     * 设置支付结果的回调
     * @param resultListener
     */
    public void setOnGoogleResultListener(IGooglePayResultListener resultListener){
        this.googleResultListener  = resultListener;
    }

    /**
     * 支付宝支付
     */
    public void  aliPay(){
        if (aliOrderInfo == null){
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IAliOrderInfo 接口 同时调用setOrderInfo方法");
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) YiBaPayConfig.getContext());
                Map<String, String> result = alipay.payV2(aliOrderInfo.getAlipayInfo(),true);

                Message msg = handler.obtainMessage();
                msg.what = ALI_PAY;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(runnable);
        payThread.start();
    }

    /**
     * 微信支付
     */
    public void wxPay(){
        if (wxOrderInfo == null){
            throw new NullPointerException("wxOrderInfo  为商品信息不能为空，需要实现 IWxOrderInfo 接口 同时调用setOrderInfo方法");
        }
        IWXAPI msgApi = WXAPIFactory.createWXAPI(YiBaPayConfig.getContext(), null);
        msgApi.registerApp(YiBaPayConfig.getWxAppId());
        PayReq request = new PayReq();
        request.appId = YiBaPayConfig.getWxAppId();
        request.partnerId = wxOrderInfo.getWxpayInfo().partnerid;
        request.prepayId = wxOrderInfo.getWxpayInfo().prepayid;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = wxOrderInfo.getWxpayInfo().noncestr;
        request.timeStamp = String.valueOf(wxOrderInfo.getWxpayInfo().timestamp);
        request.sign = wxOrderInfo.getWxpayInfo().sign;
        msgApi.sendReq(request);

    }



    /**
     * 这里随便传入一个布局
     * @param parent
     */
    public void show(View parent, final OnGenerateOrderCallback callback){
        final PayWindow payWindow = new PayWindow(YiBaPayConfig.getContext());
        payWindow.showAtLocation(parent);
        payWindow.setPayListener(new PayWindow.onPayListener() {
            @Override
            public void aliPay() {
                if (callback != null) {
                    callback.generateAliOrder();
                }
                payWindow.dismiss();
            }

            @Override
            public void wxPay() {
                if (callback != null) {
                    callback.generateWxOrder();
                }
                payWindow.dismiss();
            }

            @Override
            public void stripePay() {
                if (callback != null){
                    callback.generateStripeOrder();
                }
                payWindow.dismiss();
            }
        });
    }

    public interface OnGenerateOrderCallback{
        void generateAliOrder();
        void generateWxOrder();
        void generateStripeOrder();
    }

    public BroadcastReceiver wxPayResult = new  BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");
            Message msg = handler.obtainMessage();
            msg.what = WEIXIN_PAY;
            msg.obj = code;
            handler.sendMessage(msg);
            Log.i("onPayFinish,errCode=","Code  =  " +code);
        }
    };
    private GooglePay googlePay;

    public void initGooglePay(Activity activity){
        googlePay = new GooglePay(activity, YiBaPayConfig.getGgAppId(),handler);
    }
    public void  GgBuyGoods(Activity activity,String productId){
        if (googlePay!=null){
            googlePay.buyGoods(activity,productId,activity.getPackageName(),"");
        }else{
            Log.i("tag","please init google pay");
        }

    }
    private void unRegisterGoogleBroadCast(){
        if (googlePay!=null){
            googlePay.unRegister();
            googlePay.OnDispose();
        }
    }


    /**
     * 销毁引用
     */
    public void DestoryQuote(){
        unRegisterWxBroadCast();
        unRegisterGoogleBroadCast();
        googleResultListener = null;
        aliResultListener = null;
        wxResultListener = null;

    }
    /**
     * 广播销毁,这个方法在Activity销毁的时候需要调用。
     */
    private void unRegisterWxBroadCast(){
        if (wxPayResult!=null){
            PackageManager pm = YiBaPayConfig.getContext().getPackageManager();
            Intent intent = new Intent(ACTION);
            List<ResolveInfo> list =  pm.queryBroadcastReceivers(intent,0);
            if (list!=null && !list.isEmpty()){
                YiBaPayConfig.getContext().unregisterReceiver(wxPayResult);
            }
        }
    }


    public boolean bindCallBack(int requestCode, int resultCode, Intent data){
        if (googlePay!=null){
          return   googlePay.bindCallBack(requestCode,resultCode,data);
        }
        return false;
    }


}

