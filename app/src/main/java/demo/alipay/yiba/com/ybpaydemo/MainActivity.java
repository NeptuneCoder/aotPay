package demo.alipay.yiba.com.ybpaydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yiba.ali.pay.AliPay;
import com.yiba.ali.pay.IAliResultCallback;
import com.yiba.ali.pay.IGetAliOrderInfoListener;
import com.yiba.google.pay.GooglePay;
import com.yiba.google.pay.IGooglePayResultListener;
import com.yiba.google.pay.RandomString;
import com.yiba.pay.IResultListener;

import com.yiba.pay.YiBaPayManager;
import com.yiba.pay.YiBaPayConfig;
import com.yiba.sa.pay.StripeAliPay;
import com.yiba.wx.pay.IWeiXinCallback;
import com.yiba.wx.pay.WeiXinPay;
import com.yiba.wx.pay.WxPayInfo;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
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
        YiBaPayConfig.setGgAppId(PublicKeyConfig.WxAppId);
        YiBaPayManager.getInstance().initGooglePay(this);

        OrderInfoEt = findViewById(R.id.et);
        findViewById(R.id.ali_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AliPay aliPay = new AliPay(MainActivity.this);
                aliPay.aliPay(new IAliResultCallback() {
                    @Override
                    public void onResult(Map<String, String> res) {

                    }

                    @Override
                    public String getOrderInfo() {
                        return getAlipayInfo();
                    }
                });
            }
        });
        findViewById(R.id.wx_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WeiXinPay wxPay = new WeiXinPay(MainActivity.this, YiBaPayConfig.getWxAppId(), new IWeiXinCallback() {
                    @Override
                    public WxPayInfo getWxPayInfo() {
                        return getWxPayInfo();
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

            }
        });
        final StripeAliPay stripeAliPay = new StripeAliPay(this, PublicKeyConfig.STRIPE_ID);
        findViewById(R.id.stripe_ali_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *    final @NonNull String currency,
                 final @Nullable String name,
                 final @Nullable String email,
                 final @NonNull String returnUrl
                 */
                stripeAliPay.pay(4000l, "hkd", "YH", "YANGHAI0523@gmail.com", "mycompany://alipay", new StripeAliPay.OnPayStatusListener() {
                    @Override
                    public void start() {
                        Toast.makeText(MainActivity.this, " start ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void stripeError(String e) {
                        Toast.makeText(MainActivity.this, "stripe err ,content =  " + e, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void complete() {
                        Toast.makeText(MainActivity.this, "stripe complete ,then invoke alipay api", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void aliPayFaile(int code) {
                        Toast.makeText(MainActivity.this, "ali pay err = " + code, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void aliPaySuccess(String payToken) {
                        Toast.makeText(MainActivity.this, "ali pay success", Toast.LENGTH_LONG).show();
                    }


                });
            }
        });
        YiBaPayManager.getInstance().setOnResultListener(new IResultListener() {
            @Override
            public void onAliFailed(int code) {

            }

            @Override
            public void onAliSuccess() {

            }

            @Override
            public void onGgFailed(int code) {

            }

            @Override
            public void onGgSuccess(GooglePay.OrderParam data) {

            }


            @Override
            public void onWxFailed(int code) {

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

    public String getAlipayInfo() {
        boolean rsa2 = (PublicKeyConfig.RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(PublicKeyConfig.APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? PublicKeyConfig.RSA2_PRIVATE : PublicKeyConfig.RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;
        return orderInfo;//OrderInfoEt.getText().toString().trim();
    }


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


