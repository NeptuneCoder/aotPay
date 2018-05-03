package com.yiba.wx.pay;

public interface IWeiXinCallback {

    WxPayInfo getWxPayInfo();
    void onResult(String code);
}
