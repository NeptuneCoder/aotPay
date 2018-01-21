package com.yiba.pay;

/**
 * Created by yh on 2018/1/17.
 */

public interface IAliResultListener {

    void  onAliFailed(int code);

    void onAliSuccess();

}
