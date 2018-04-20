package com.yiba.pay;

public interface IGooglePayStatusListener {

    void callBackStatus(int status);

    void callBackStatus(GooglePay.OrderParam data);
}
