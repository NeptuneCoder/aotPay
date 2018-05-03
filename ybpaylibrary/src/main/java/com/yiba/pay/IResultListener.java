package com.yiba.pay;

import com.google.pay.IGooglePayResultListener;
import com.weixin.pay.IWxResult;
import com.yiba.ali.pay.IAliResult;

/**
 * Created by yh on 2017/10/23.
 */

public interface IResultListener extends IAliResult, IWxResult, IGooglePayResultListener {

}
