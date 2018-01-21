package com.yiba.pay;

/**
 * Created by yh on 2018/1/17.
 */

public interface IGooglePayResultListener {
    /**
     *
     * @param code 为错误提示的code
     * code :
     */

    int INIT_FAILED = 10001; //初始化谷歌服务失败
    int UN_LOGIN = 10003; //检查google play商店是否登录，获取不了远程的pendingIntent

    int SUCCESS = 2000; //初始化错误

    void  onGgFailed(int code);

    void onGgSuccess(String result);
}
