package com.weixin.pay;

public interface IWeiXinCallback {

    WxPayInfo getWxPayInfo();
    void onResult(String code);
}
