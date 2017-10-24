package com.yiba.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;

import java.util.Map;


/**
 * Created by yh on 2017/10/23.
 */

public class YiBaPayManager {

    private static final int ALI_PAY = 001;
    private static final int WEIXIN_PAY = 002;
    private static final int STRIPE_PAY = 003;
    private IAliOrderInfo aliOrderInfo;
    private IWxOrderInfo wxOrderInfo;
    private static IResultListener resultListener;


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
                           resultListener.onSuccess();
                       }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (resultListener != null){
                            resultListener.onFailed();
                        }
                    }
                    break;
                case WEIXIN_PAY:


                    break;
                case STRIPE_PAY:

                    break;
            }

        }
    };


    private YiBaPayManager(){

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
    public void setResultListener(IResultListener resultListener){
        this.resultListener  = resultListener;
    }
    private void  alipay(){
        if (aliOrderInfo == null){
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IWxOrderInfo 接口 同时调用setOrderInfo方法");
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

    private void wxpay(){

    }
    private void stripepay(){

    }

    /**
     * 这里随便传入一个布局
     * @param parent
     */
    public void show(View parent){
        PayWindow payWindow = new PayWindow(YiBaPayConfig.getContext());
        payWindow.showAtLocation(parent);
        payWindow.setPayListener(new PayWindow.onPayListener() {
            @Override
            public void aliPay() {
                alipay();
            }

            @Override
            public void wxPay() {
                wxpay();
            }

            @Override
            public void stripePay() {
                stripepay();
            }
        });
    }
}
