package com.ali.pay;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.yiba.pay.YiBaPayConfig;

import java.util.Map;

public class AliPay {

    private final IAliResultCallback iAliResultCallback;
    private PayTask alipay;
    private Runnable runnable;

    public AliPay(IAliResultCallback iAliResultCallback) {
        this.iAliResultCallback = iAliResultCallback;

    }

    private void init() {
        if (iAliResultCallback == null || iAliResultCallback.getOrderInfo() == null) {
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IGetAliOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        alipay = new PayTask((Activity) YiBaPayConfig.getContext());
        runnable = new Runnable() {
            @Override
            public void run() {
                Map<String, String> result = alipay.payV2(iAliResultCallback.getOrderInfo(), true);
                iAliResultCallback.onResult(result);
            }
        };
    }

    public void aliPay() throws NullPointerException {
        init();
        Thread payThread = new Thread(runnable);
        payThread.start();
    }
}