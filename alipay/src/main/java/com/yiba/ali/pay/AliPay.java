package com.yiba.ali.pay;

import android.app.Activity;
import android.content.Context;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class AliPay {

    private final Activity activity;
    private IAliResultCallback iAliResultCallback;
    private PayTask alipay;
    private Runnable runnable;

    public AliPay(Activity activity) {
        this.activity = activity;

    }

    private void init(Activity activity) {
        if (iAliResultCallback == null) {
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IGetAliOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        if (alipay != null) {
            alipay = new PayTask(activity);
            runnable = new Runnable() {
                @Override
                public void run() {
                    Map<String, String> result = alipay.payV2(iAliResultCallback.getOrderInfo(), true);
                    iAliResultCallback.onResult(result);
                }
            };
        }
    }

    public void aliPay(IAliResultCallback iAliResultCallback) throws NullPointerException {
        this.iAliResultCallback = iAliResultCallback;
        init(activity);
        Thread payThread = new Thread(runnable);
        payThread.start();
    }
}