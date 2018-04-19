package com.google.pay;

import com.yiba.pay.GooglePay;

public interface IabHelperCallbackListener {

    void onGgSuccess(GooglePay.OrderParam data);
}
