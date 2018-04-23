package com.ali.pay;

/**
 * Created by yh on 2018/1/17.
 */

public interface IAliResult {

    void  onAliFailed(int code);

    void onAliSuccess();

}
