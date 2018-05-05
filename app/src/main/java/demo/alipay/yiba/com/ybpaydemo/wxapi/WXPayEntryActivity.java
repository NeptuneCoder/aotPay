package demo.alipay.yiba.com.ybpaydemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiba.wx.pay.WeiXinPay;

import demo.alipay.yiba.com.ybpaydemo.R;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;
    public static final String APP_ID = "wxe60d1b76fdcf5504";//wxe60d1b76fdcf5504

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);
        iwxapi = WXAPIFactory.createWXAPI(this, APP_ID);
        iwxapi.handleIntent(getIntent(), this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("baseResp,errCode=", "onPayFinish,errCode=" + baseReq.openId);
    }

    @Override
    public void onResp(BaseResp baseResp) {
//        Log.i("baseResp,errCode=", "onPayFinish,errCode=" + baseResp.errCode);
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent(WeiXinPay.ACTION);
//            Log.i("onPayFinish,errCode=", "onPayFinish,errCode=" + baseResp.errCode);
            if (baseResp.errCode == 0) {
                intent.putExtra("code", "0");
            } else if (baseResp.errCode == -2) {
                intent.putExtra("code", "2");
            } else if (baseResp.errCode == -3) {
                intent.putExtra("code", "3");
            } else {
                intent.putExtra("code", "4");
            }
            sendBroadcast(intent);
            this.finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

}
