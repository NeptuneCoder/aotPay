package com.yiba.pay;

import pay.IAliResult;
import com.yiba.google.pay.IGooglePayResultListener;
import com.yiba.wx.pay.IWxResult;

/**
 * Created by yh on 2017/10/23.
 */

public interface IResultListener extends IAliResult, IWxResult, IGooglePayResultListener {

}
