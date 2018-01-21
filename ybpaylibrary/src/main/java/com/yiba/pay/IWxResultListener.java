package com.yiba.pay;

/**
 * Created by yh on 2018/1/17.
 */

public interface IWxResultListener {
    void  onWxFailed(int code);

    void onWxSuccess();
}
