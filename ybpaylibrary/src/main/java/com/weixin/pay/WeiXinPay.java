package com.weixin.pay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiba.pay.YiBaPayConfig;

import java.util.List;

public class WeiXinPay {

    private final IWeiXinCallback iWeiXinCallback;
    private static final String BROADCAST_PERMISSION_DISC = "com.yiba.permissions.YiBaPay";
    public static final String ACTION = "com.yiba.pay.wxResult";
    private IntentFilter filter;

    public WeiXinPay(IWeiXinCallback iWeiXinCallback) {
        this.iWeiXinCallback = iWeiXinCallback;
        filter = new IntentFilter();
        filter.addAction(ACTION);
        YiBaPayConfig.getContext().registerReceiver(wxPayResult, filter, BROADCAST_PERMISSION_DISC, null);

    }

    public void wxPay() {
        if (iWeiXinCallback == null) {
            throw new NullPointerException("wxOrderInfo  为商品信息不能为空，需要实现 IGetWxOrderInfoListener 接口 同时调用setOrderInfo方法");
        }
        IWXAPI msgApi = WXAPIFactory.createWXAPI(YiBaPayConfig.getContext(), null);
        msgApi.registerApp(YiBaPayConfig.getWxAppId());
        PayReq request = new PayReq();
        request.appId = YiBaPayConfig.getWxAppId();
        request.partnerId = iWeiXinCallback.getWxPayInfo().partnerid;
        request.prepayId = iWeiXinCallback.getWxPayInfo().prepayid;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = iWeiXinCallback.getWxPayInfo().noncestr;
        request.timeStamp = String.valueOf(iWeiXinCallback.getWxPayInfo().timestamp);
        request.sign = iWeiXinCallback.getWxPayInfo().sign;
        msgApi.sendReq(request);
    }

    public BroadcastReceiver wxPayResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");
            if (iWeiXinCallback != null) {
                iWeiXinCallback.onResult(code);
            }
            Log.i("onPayFinish,errCode=", "Code  =  " + code);
        }
    };

    public void unRegisterWxBroadCast() {
        if (wxPayResult != null) {
            Context cnt = YiBaPayConfig.getContext();
            if (cnt == null) return;
            PackageManager pm = cnt.getPackageManager();
            Intent intent = new Intent(ACTION);
            List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
            if (list != null && !list.isEmpty()) {
                YiBaPayConfig.getContext().unregisterReceiver(wxPayResult);
            }
        }
    }


}
