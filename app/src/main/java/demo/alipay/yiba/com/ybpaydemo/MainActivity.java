package demo.alipay.yiba.com.ybpaydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ali.pay.AliPay;
import com.ali.pay.IAliResultCallback;
import com.ali.pay.IGetAliOrderInfoListener;
import com.google.pay.RandomString;
import com.google.pay.GooglePay;
import com.google.pay.IGooglePayResultListener;
import com.weixin.pay.IGetWxOrderInfoListener;
import com.weixin.pay.IWeiXinCallback;
import com.weixin.pay.WeiXinPay;
import com.yiba.pay.IResultListener;
import com.weixin.pay.WxPayInfo;
import com.yiba.pay.YiBaPayManager;
import com.yiba.pay.YiBaPayConfig;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements IGetAliOrderInfoListener, IGetWxOrderInfoListener {
    /**
     * 支付宝支付业务：入参app_id
     */
    private EditText OrderInfoEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YiBaPayConfig.setContext(this);
        YiBaPayConfig.setGgAppId(PublicKeyConfig.GOOGLE_ID);
        YiBaPayManager.getInstance().initGooglePay(this);

        OrderInfoEt = findViewById(R.id.et);

        findViewById(R.id.ali_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                YiBaPayManager.getInstance().aliPay(new IGetAliOrderInfoListener() {
//                    @Override
//                    public String getAlipayInfo() {
//                        return  OrderInfoEt.getText().toString().trim();
//                    }
//                });
                AliPay aliPay = new AliPay(new IAliResultCallback() {
                    @Override
                    public void onResult(Map<String, String> res) {

                    }

                    @Override
                    public String getOrderInfo() {
                        return null;
                    }
                });
                aliPay.aliPay();
            }
        });
        findViewById(R.id.wx_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                YiBaPayManager.getInstance().wxPay(new IGetWxOrderInfoListener() {
//                    @Override
//                    public WxPayInfo getWxPayInfo() {
//
//                        return null;
//                    }
//                });

                WeiXinPay wxPay = new WeiXinPay(new IWeiXinCallback() {
                    @Override
                    public WxPayInfo getWxPayInfo() {
                        return null;
                    }

                    @Override
                    public void onResult(String code) {

                    }
                });
                wxPay.wxPay();
            }
        });

        findViewById(R.id.google_pay_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RandomString randomString = new RandomString(36);
                YiBaPayManager.getInstance().subsGood(MainActivity.this, "svip_1_month", randomString.nextString().toString());
            }
        });

        findViewById(R.id.google_pay_inapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RandomString randomString = new RandomString(36);

                YiBaPayManager.getInstance().GgBuyGoods(MainActivity.this, "vip_1_month", randomString.nextString().toString());
            }
        });


        findViewById(R.id.select_Pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                YiBaPayManager.getInstance().GgBuyGoods(MainActivity.this, "vip_1_month");
////                if (!OrderInfoEt.getText().toString().trim().isEmpty()){
////
////                }else{
////                    Toast.makeText(MainActivity.this,"订单信息不能为空",Toast.LENGTH_LONG).show();
////                }
                YiBaPayManager.getInstance().show(findViewById(R.id.select_Pay), new YiBaPayManager.OnGenerateOrderCallback() {
                    @Override
                    public void generateAliOrder() {

                    }

                    @Override
                    public void generateWxOrder() {

                    }

                    @Override
                    public void generateStripeOrder() {

                    }
                });
            }
        });
        YiBaPayManager.getInstance().setOnResultListener(new IResultListener() {
            @Override
            public void onAliFailed(int i) {

            }

            @Override
            public void onAliSuccess() {

            }

            @Override
            public void onGgFailed(int i) {

            }

            @Override
            public void onGgSuccess(GooglePay.OrderParam data) {

            }


            @Override
            public void onWxFailed(int i) {

            }

            @Override
            public void onWxSuccess() {

            }
        });

        YiBaPayManager.getInstance().setOnGoogleResultListener(new IGooglePayResultListener() {
            @Override
            public void onGgFailed(int code) {
                Toast.makeText(MainActivity.this, "code = " + code, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGgSuccess(GooglePay.OrderParam data) {
                Log.i("tag", "");
            }


        });

    }


    /**
     * 生成订单信息
     *
     * @return
     */
    @Override
    public String getAlipayInfo() {
        boolean rsa2 = (PublicKeyConfig.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(PublicKeyConfig.APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? PublicKeyConfig.RSA2_PRIVATE : PublicKeyConfig.RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;
        return OrderInfoEt.getText().toString().trim();
    }

    @Override
    public WxPayInfo getWxPayInfo() {
        return null;
    }


//    @Override
//    public String getWxPayInfo() {
//        return "";
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!YiBaPayManager.getInstance().bindCallBack(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }


        Log.i("aCTIVITY requestCode", "requestCode = " + requestCode + "resultCode = " + resultCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YiBaPayManager.getInstance().DestoryQuote();
    }
}


