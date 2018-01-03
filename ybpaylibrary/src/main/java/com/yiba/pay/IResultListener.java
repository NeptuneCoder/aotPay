package com.yiba.pay;

/**
 * Created by yh on 2017/10/23.
 */

public interface IResultListener {

    void  onAliFailed(int code);

    void onAliSuccess();

    void  onWxFailed(int code);

    void onWxSuccess();
}
