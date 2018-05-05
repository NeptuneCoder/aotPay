package com.yiba.ali.pay;

import android.app.Activity;
import android.content.Context;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class AliPay {

    private final IAliResultCallback iAliResultCallback;
    private PayTask alipay;
    private Runnable runnable;

    public AliPay(Activity activity, IAliResultCallback iAliResultCallback) {
        this.iAliResultCallback = iAliResultCallback;
        init(activity);
    }

    private void init(Activity activity) {
        if (iAliResultCallback == null ) {
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IGetAliOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        alipay = new PayTask(activity);
        runnable = new Runnable() {
            @Override
            public void run() {
                Map<String, String> result = alipay.payV2(iAliResultCallback.getOrderInfo(), true);
                iAliResultCallback.onResult(result);
            }
        };
    }

    public void aliPay() throws NullPointerException {
        Thread payThread = new Thread(runnable);
        payThread.start();
    }
}