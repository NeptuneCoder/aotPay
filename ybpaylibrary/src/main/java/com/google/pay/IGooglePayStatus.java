package com.google.pay;


public interface IGooglePayStatus {

    void callBackStatus(int status);

    void callBackStatus(GooglePay.OrderParam data);
}
