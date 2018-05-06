package com.yiba.sa.pay;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yiba.ali.pay.AliPay;
import com.yiba.ali.pay.IAliResultCallback;
import com.yiba.ali.pay.PayResult;
import com.yiba.stripe.pay.StripePay;

import java.util.Map;

import com.stripe.android.model.Source;

/**
 * test rebase,this  is master branch commit
 */
public class StripeAliPay {
    private final Activity activity;
    private final String publishableKey;
    private String key;
    private StripePay stripePay;
    private Source source;
    private AliPay aliPay;
    private OnPayStatusListener listener;

    public StripeAliPay(final Activity activity, String publishableKey) {
        this.activity = activity;
        this.publishableKey = publishableKey;

        stripePay = new StripePay(activity, publishableKey);
        aliPay = new AliPay(activity);
    }

    public void pay(final @IntRange(from = 0) long amount,
                    final @NonNull String currency,
                    final @Nullable String name,
                    final @Nullable String email,
                    final @NonNull String returnUrl, final OnPayStatusListener listener) {
        this.listener = listener;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.start();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    source = stripePay.generateKey(amount, currency, name, email, returnUrl);
                    Map<String, Object> result = source.getSourceTypeData();
                    key = (String) result.get("data_string");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.complete();
                            }
                        }
                    });
                    aliPay.aliPay(new IAliResultCallback() {
                        @Override
                        public void onResult(final Map<String, String> res) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    aliPayResult(res);
                                }
                            });
                        }

                        @Override
                        public String getOrderInfo() {
                            return key;
                        }
                    });
                } catch (final Exception e) {

//                        if (e instanceof com.stripe.android.exception.APIException){
//                        }else if (e instanceof com.stripe.android.exception.AuthenticationException){
//                        }else if (e instanceof com.stripe.android.exception.InvalidRequestException){
//                        }else if (e instanceof com.stripe.android.exception.APIConnectionException){
//                        }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.stripeError(e.toString());
                        }
                    });

                }
            }
        }).start();

    }

    private void aliPayResult(Map<String, String> res) {
        PayResult payResult = new PayResult(res);
        /**
         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            if (listener != null && source != null) {
                listener.aliPaySuccess(source.getId());
            }
        } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            if (listener != null) {
                listener.aliPayFaile(Integer.parseInt(resultStatus.trim()));
            }
        }
    }

    public interface OnPayStatusListener {
        void start();

        void stripeError(String e);

        void complete();

        void aliPayFaile(int code);

        void aliPaySuccess(String payToken);
    }
}
