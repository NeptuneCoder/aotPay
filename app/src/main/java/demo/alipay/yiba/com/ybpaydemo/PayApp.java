package demo.alipay.yiba.com.ybpaydemo;

import android.app.Application;

import com.yiba.pay.YiBaPayConfig;

/**
 * Created by yh on 2017/10/23.
 */

public class PayApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        YiBaPayConfig.setContext(getApplicationContext());
    }
}
