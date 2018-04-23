package com.weixin.pay;

/**
 * Created by yh on 2018/1/17.
 */

public interface IWxResult {
    void  onWxFailed(int code);

    void onWxSuccess();
}
