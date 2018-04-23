package com.yiba.pay;

import com.ali.pay.IAliResult;
import com.google.pay.IGooglePayResultListener;
import com.weixin.pay.IWxResult;

/**
 * Created by yh on 2017/10/23.
 */

public interface IResultListener extends IAliResult,IWxResult,IGooglePayResultListener {

}
