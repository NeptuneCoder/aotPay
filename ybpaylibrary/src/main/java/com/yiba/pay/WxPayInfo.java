package com.yiba.pay;

import java.io.Serializable;

/**
 * Created by yh on 2018/1/2.
 */

public class WxPayInfo implements Serializable{

    public String partnerid;
    public String prepayid;
    public String noncestr;
    public String timestamp;
    public String sign;
}
