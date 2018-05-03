package com.yiba.ali.pay;

import java.util.Map;

/**
 * Created by yh on 2018/1/17.
 */

public interface IAliResultCallback {

    void onResult(Map<String, String> res);

    String getOrderInfo();

}
