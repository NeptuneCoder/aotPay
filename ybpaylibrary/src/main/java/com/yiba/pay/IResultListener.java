package com.yiba.pay;

import com.weixin.pay.IWxResult;
import com.yiba.ali.pay.IAliResult;
import com.yiba.google.pay.IGooglePayResultListener;

/**
 * Created by yh on 2017/10/23.
 */

public interface IResultListener extends IAliResult, IWxResult, IGooglePayResultListener {

}
