package demo.alipay.yiba.com.ybpaydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yiba.pay.IAliOrderInfo;
import com.yiba.pay.IGooglePayResultListener;
import com.yiba.pay.IResultListener;
import com.yiba.pay.IWxOrderInfo;
import com.yiba.pay.WxPayInfo;
import com.yiba.pay.YiBaPayConfig;
import com.yiba.pay.YiBaPayManager;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements IAliOrderInfo, IWxOrderInfo {
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2017102309473363";
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1Asmm936+SnW5lHWLn3bfzN+ZyGlQ7OiDJ6EC7vMd3qXoYKzuJTiU6bU2/LNqfUgF4chHjH8iZ6nXwLYm/iGk5Ynigr+3B31UeFoAS/8MoubBLHblOUVn5cDlGiXktE+/Jwns4ACCd9Q8cpGPtBomnlgLC5M59MYM6KSv8O6nd18KIFQb4/GG6mGujKIhe7qFUHcRkG3DVU1lSqTQh8Z7PbCH0q9ht361Y0tm1rFcgjHjJ6YLL3elBb5wNz8ZaMZbhna43SB7ecMATsCOFLPhyPVK9OrhPBKTlqdJro1ITO0bgjxkxyiHjiPcTQuAzd8AJRqWjzxPa9ZXz44Rr63BAgMBAAECggEAXZ9Qf3yZYIEQvOjtl0bmbEECEf9XgZTXeibAH81Jj9R+CWAfcZls15C9uvAfOGAOJYSvW1pu03O8pCw3ypk8+5YBqFeqI1fLWipZmrez9mw3YAjIgi0KGf/6skTA/rbIXH0rpAoEGXczHgTABwzHTdwue6AECUDyQvD9ZUb/mTvE3XtZaX0CJKa2wJFgMDMZks0hVya78i6JmRqdJuTZiHssPRJj4Yj+olR2jQNatnLAi56b3ftNR4rTT37cA3qnltIPqcpYhVq9pm6OnYZQ1C5WXH+3+82/f3rY4BhJdxIH+91+Qwofvfi1reGo8qJv8YCQBr5vmlRFpsg3WRbDAQKBgQDgPPVa6Nl+Sz3D7vLTh/km/AOB086JgiGclZn+I/Oix2ci0dQddkCo6ldA/VocRQWPb6l2QDngR7VpXd12sn3pOp+dQ/JozRl0ZtFbbUvlhEVBDqa8cuhg5f4Xs0sI7PcL6sNdlhhsa4IL2VbKwHMTGeZKAb6V3t0VUsiefpx9EQKBgQDOpmCvphq2hs44ib6Uusp0A4lj7Oy2XhvCVdb8DIfPlxLaaP56z3I2O8OlM0EeIxlIw890BfbQEXr/DmRcF5Fp97TnCW6+inh7XFZdS7Rf3xxLQozv8HpC5j45ql0CoN2b6A8JdWtLZBHtQtoL4hSOScu8jOGzWxo+tFbkFc3lsQKBgARzkVdVqgOk0LwUAUQLvfl1JUitgLsgFzS5j44I/qwrkzOQwMo4772qsUK8BM7s29hmGhIb4ko+gi5uS5gwoOlBXavMjoJgF/JEnEFZYRlUT9+jv9Gb4lsSFcI4r/OkaJ8W8Pvpn+B/HdzIWr25pk8pw3WSpUrqWq8yroLe9pJRAoGBAKQoXPQLknreuaEGIyQJAlyIlrKPejcgqRKGmCJfXd+VVj+0Wjt64XKdpTxta9Qh86rvCbRULI165WcJqsXRXw173fQOdS/d1fnBD0ZLpjzmgpZyhnzwXl8YgePTJtG4snJwNMnBwfsAwpDko0v/S28AxlzA2WZd/4pCPigv/gDxAoGAFe4jkBcWQZSL/Vg4be9pfxUDBz34eM9NfYpDZjIls8WWghppsDl4933SwZrkzydZ4qeBEVHxOCCrfYGIjnZKta/laFfD1K6S0dcaB7NuoWATAjoxZtsNTo0Iegfi5ruC+GGlV8mJ0bhoPW6ljmAmQijLseNGoXTwK/h/to3ANGo=";
    public static final String RSA_PRIVATE = "";
    private EditText OrderInfoEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YiBaPayConfig.setContext(this);
        YiBaPayConfig.setGgAppId("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApjWy+r9s6ncuh2l8OK59KrvySuTUQi5Zc1Sel/y2nVXh+7rEAVNV+Ndz75eJeT+mA3Y3uzRAfCuRR6lziyhE+5Jj330JtoWvi4SNJghVMSTs/uxK1B/Jg1GVUsYzC93QciBIEch22hCZWI93Gjq5UJ3OC5uy45YwIS4bYnjv2n7H37QSlfE1pzlNq8HktULpfD1lA6Sdc8NNRDl3c5OfUzIwYh6d2ErDjEa0EnIEksGBHlo3/zsgTwuG4Fm1DugNA/uQbvaps3tFSzc55afFWPuTtzVEVYqAvP2hJglklmmz0oZNWK8GYPg4iEeXlFWGSuWRT04zYVgJFj0LbJkGWQIDAQAB");
        YiBaPayManager.getInstance().initGooglePay(this);
        final Button showPay = findViewById(R.id.showPay);


        OrderInfoEt = findViewById(R.id.et);
        YiBaPayManager.getInstance().setAliOrderInfo(this);
        YiBaPayManager.getInstance().setWxOrderInfo(this);


        showPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YiBaPayManager.getInstance().GgBuyGoods(MainActivity.this, "vip_1_month");
//                if (!OrderInfoEt.getText().toString().trim().isEmpty()){
//
//                }else{
//                    Toast.makeText(MainActivity.this,"订单信息不能为空",Toast.LENGTH_LONG).show();
//                }

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
            public void onGgSuccess(int resultCode, int responseCode, String dataSignature, String purchaseData) {

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
            public void onGgSuccess(int resultCode, int responseCode, String dataSignature, String purchaseData) {

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
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String privateKey = rsa2 ? d : RSA_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
        return OrderInfoEt.getText().toString().trim();
    }

    @Override
    public WxPayInfo getWxpayInfo() {
        return null;
    }


//    @Override
//    public String getWxpayInfo() {
//        return "";
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        YiBaPayManager.getInstance().bindCallBack(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("aCTIVITY requestCode", "requestCode = " + requestCode + "resultCode = " + resultCode);
    }

    @Override
    protected void onDestroy() {
        YiBaPayManager.getInstance().DestoryQuote();
        super.onDestroy();
    }
}


