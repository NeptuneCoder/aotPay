package com.yiba.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import pay.AliPay;
import pay.IAliResult;
import pay.IAliResultCallback;
import pay.IGetAliOrderInfoListener;
import pay.PayResult;

import com.yiba.google.pay.GooglePay;
import com.yiba.google.pay.IGooglePayResultListener;
import com.yiba.google.pay.IGooglePayStatus;
import com.yiba.wx.pay.IGetWxOrderInfoListener;
import com.yiba.wx.pay.IWeiXinCallback;
import com.yiba.wx.pay.IWxResult;
import com.yiba.wx.pay.WeiXinPay;
import com.yiba.wx.pay.WxPayInfo;

import java.util.Map;


/**
 * Created by yh on 2017/10/23.
 */

public class YiBaPayManager {

    private static final int ALI_PAY = 001;
    private static final int WEIXIN_PAY = 002;
    public static final int GOOGLE_PAY = 003;
    private static IResultListener resultListener;
    private static IAliResult aliResultListener;
    private static IWxResult wxResultListener;
    private static IGooglePayResultListener googleResultListener;

    private YiBaPayManager() {
        //初始化googepay
        if (SingleTonHolder.INSTANCE != null) {
            throw new IllegalArgumentException("不允许反射");
        }
    }


    private static class SingleTonHolder {
        private SingleTonHolder() {

        }

        private final static YiBaPayManager INSTANCE = new YiBaPayManager();
    }

    public static YiBaPayManager getInstance() {
        return SingleTonHolder.INSTANCE;
    }


    private static Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ALI_PAY:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (resultListener != null) {
                            resultListener.onAliSuccess();
                        }
                        if (aliResultListener != null) {
                            aliResultListener.onAliSuccess();
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (resultListener != null) {
                            resultListener.onAliFailed(Integer.parseInt(resultStatus.trim()));
                        }
                        if (aliResultListener != null) {
                            aliResultListener.onAliFailed(Integer.parseInt(resultStatus.trim()));
                        }
                    }
                    break;
                case WEIXIN_PAY:
                    String code = (String) msg.obj;
                    if ("0".equals(code)) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (resultListener != null) {
                            resultListener.onWxSuccess();
                        }
                        if (wxResultListener != null) {
                            wxResultListener.onWxSuccess();
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (resultListener != null) {
                            resultListener.onWxFailed(Integer.parseInt(code.trim()));
                        }
                        if (wxResultListener != null) {
                            wxResultListener.onWxFailed(Integer.parseInt(code.trim()));
                        }
                    }

                    break;
                case GOOGLE_PAY:
                    if (msg.obj instanceof Integer) {
                        int ggCode = (int) msg.obj;
                        if (ggCode == IGooglePayResultListener.INIT_FAILED) {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (resultListener != null) {
                                resultListener.onGgFailed(IGooglePayResultListener.INIT_FAILED);
                            }
                            if (googleResultListener != null) {
                                googleResultListener.onGgFailed(IGooglePayResultListener.INIT_FAILED);
                            }
                        } else if (ggCode == IGooglePayResultListener.UN_LOGIN) {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (resultListener != null) {
                                resultListener.onGgFailed(IGooglePayResultListener.UN_LOGIN);
                            }
                            if (googleResultListener != null) {
                                googleResultListener.onGgFailed(IGooglePayResultListener.UN_LOGIN);
                            }
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (resultListener != null) {
                                resultListener.onGgFailed(ggCode);
                            }
                            if (googleResultListener != null) {
                                googleResultListener.onGgFailed(ggCode);
                            }
                        }
                    } else if (msg.obj instanceof GooglePay.OrderParam) {
                        GooglePay.OrderParam result = (GooglePay.OrderParam) msg.obj;
                        if (googleResultListener != null) {
                            googleResultListener.onGgSuccess(result);
                        }
                    }

                    break;
            }
            return false;
        }
    });


    /**
     * 设置支付结果的回调
     *
     * @param resultListener
     */
    public void setOnResultListener(IResultListener resultListener) {
        this.resultListener = resultListener;
    }

    /**
     * 设置支付结果的回调
     *
     * @param resultListener
     */
    public void setOnGoogleResultListener(IGooglePayResultListener resultListener) {
        this.googleResultListener = resultListener;
    }

    /**
     * 支付宝支付
     */
    public void aliPay(AliPay alipay, final IGetAliOrderInfoListener aliOrderInfo) {
        if (aliOrderInfo == null) {
            throw new NullPointerException("aliOrderInfo  为商品信息不能为空，需要实现 IGetAliOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        alipay.aliPay(new IAliResultCallback() {
            @Override
            public void onResult(Map<String, String> res) {
                Message msg = handler.obtainMessage();
                msg.what = ALI_PAY;
                msg.obj = res;
                handler.sendMessage(msg);
            }

            @Override
            public String getOrderInfo() {
                return aliOrderInfo.getAlipayInfo();
            }

        });
    }

    /**
     * 微信支付
     */
    private WeiXinPay weiXinPay;

    public void wxPay(WeiXinPay weiXinPay, final IGetWxOrderInfoListener wxOrderInfo) {
        if (wxOrderInfo == null) {
            throw new NullPointerException("wxOrderInfo  为商品信息不能为空，需要实现 IGetWxOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        weiXinPay.setiWeiXinCallback(new IWeiXinCallback() {
            @Override
            public WxPayInfo getWxPayInfo() {
                return wxOrderInfo.getWxPayInfo();
            }

            @Override
            public void onResult(String code) {
                Message msg = handler.obtainMessage();
                msg.what = WEIXIN_PAY;
                msg.obj = code;
                handler.sendMessage(msg);
            }
        });
        weiXinPay.wxPay();

    }


    private GooglePay googlePay;

    public void initGooglePay(GooglePay googlePay) {
        this.googlePay = googlePay;
        googlePay.setIGooglePayStatus(new IGooglePayStatus() {

            @Override
            public void callBackStatus(int status) {
                Message msg = handler.obtainMessage();
                msg.what = GOOGLE_PAY;
                msg.obj = status;
                handler.sendMessage(msg);
            }

            @Override
            public void callBackStatus(GooglePay.OrderParam data) {
                Message msg = handler.obtainMessage();
                msg.what = GOOGLE_PAY;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        });
    }

    public void subsGood(Activity activity, String productId, String developerPayload) {
        if (googlePay != null) {
            googlePay.launchSubscriptionPurchaseFlow(activity, productId, developerPayload);
        } else {
            Log.i("tag", "please init google pay");
        }

    }

    public void GgBuyGoods(Activity activity, String productId, String developerPayload) {
        if (googlePay != null) {
            googlePay.buyGoods(activity, productId, developerPayload);
        } else {
            Log.i("tag", "please init google pay");
        }

    }


    @Deprecated
    public void GgBuyGoods(Activity activity, String productId) {
        if (googlePay != null) {
            googlePay.buyGoods(activity, productId, "");
        } else {
            Log.i("tag", "please init google pay");
        }

    }

    private void freeGooglePayBroadCast(Context context) {
        if (googlePay != null) {
            googlePay.unRegister(context);
            googlePay.OnDispose();
        }

    }


    /**
     * 销毁引用
     */
    synchronized public void DestoryQuote(Context context) {
        if (weiXinPay != null) {
            weiXinPay.unRegisterWxBroadCast();
        }
        freeGooglePayBroadCast(context);
        googleResultListener = null;
        aliResultListener = null;
        wxResultListener = null;
        YiBaPayConfig.setContext(null);
        YiBaPayConfig.setWxAppId(null);
        YiBaPayConfig.setGgAppId(null);

    }


    public boolean bindCallBack(int requestCode, int resultCode, Intent data) {
        if (googlePay != null) {
            return googlePay.bindCallBack(requestCode, resultCode, data);
        }
        return false;
    }


}

