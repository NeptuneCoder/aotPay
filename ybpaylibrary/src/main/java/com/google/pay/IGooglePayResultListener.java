package com.google.pay;



/**
 * Created by yh on 2018/1/17.
 */

public interface IGooglePayResultListener {
    /**
     * @param code 为错误提示的code
     * code :
     */

    int INIT_FAILED = 10001; //初始化谷歌服务失败
    int CONFIRM_INVENTORY_FAILURE = 10006; //初始化谷歌服务失败
    int UN_LOGIN = 10003; //检查google play商店是否登录，获取不了远程的pendingIntent
    int CANCEL = 10004; //初始化谷歌服务失败)
    int SUCCESS = 2000; //初始化错误
    int INIT_SERVICE_FAILED = 10005; //初始化谷歌服务失败
    int DATA_IS_NULL = 10007; //初始化谷歌服务失败
    int NO_SPECIFIED_ITEM = 10008; //检查google play商店是否登录，获取不了远程的pendingIntent

    int PAYMENT_SUCCESS = 10009;
    int PAYMENT_FAILED = 10010;
    int CONSUMED_SUCCESS = 10011;
    int CONSUMED_FAILED = 10012;

    void onGgFailed(int code);

    void onGgSuccess(GooglePay.OrderParam data);
}
