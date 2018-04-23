package com.ali.pay;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.yiba.pay.YiBaPayConfig;

import java.util.Map;

public class AliPay {

    private final IAliResultCallback iAliResultCallback;

    public AliPay(IAliResultCallback iAliResultCallback) {
        this.iAliResultCallback = iAliResultCallback;
    }

    public void aliPay() {

        if (iAliResultCallback == null || iAliResultCallback.getOrderInfo() == null) {
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IGetAliOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) YiBaPayConfig.getContext());
                Map<String, String> result = alipay.payV2(iAliResultCallback.getOrderInfo(), true);
                iAliResultCallback.onResult(result);
            }
        };

        Thread payThread = new Thread(runnable);
        payThread.start();

    }
}
