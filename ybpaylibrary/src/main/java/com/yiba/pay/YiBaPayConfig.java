package com.yiba.pay;

import android.content.Context;

/**
 * Created by yh on 2017/10/23.
 */

public class YiBaPayConfig {

    public static Context mcontext;

    public  static void setContext(Context context){
        mcontext = context;
    }

    public static Context getContext(){

        if (mcontext == null){
            throw  new NullPointerException("please init context");
        }

        return mcontext;
    }
}
