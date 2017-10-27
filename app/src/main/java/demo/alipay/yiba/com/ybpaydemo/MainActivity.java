package demo.alipay.yiba.com.ybpaydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yiba.pay.IAliOrderInfo;
import com.yiba.pay.IResultListener;
import com.yiba.pay.IWxOrderInfo;
import com.yiba.pay.YiBaPayConfig;
import com.yiba.pay.YiBaPayManager;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements IAliOrderInfo,IWxOrderInfo{
    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017102309473363";
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1Asmm936+SnW5lHWLn3bfzN+ZyGlQ7OiDJ6EC7vMd3qXoYKzuJTiU6bU2/LNqfUgF4chHjH8iZ6nXwLYm/iGk5Ynigr+3B31UeFoAS/8MoubBLHblOUVn5cDlGiXktE+/Jwns4ACCd9Q8cpGPtBomnlgLC5M59MYM6KSv8O6nd18KIFQb4/GG6mGujKIhe7qFUHcRkG3DVU1lSqTQh8Z7PbCH0q9ht361Y0tm1rFcgjHjJ6YLL3elBb5wNz8ZaMZbhna43SB7ecMATsCOFLPhyPVK9OrhPBKTlqdJro1ITO0bgjxkxyiHjiPcTQuAzd8AJRqWjzxPa9ZXz44Rr63BAgMBAAECggEAXZ9Qf3yZYIEQvOjtl0bmbEECEf9XgZTXeibAH81Jj9R+CWAfcZls15C9uvAfOGAOJYSvW1pu03O8pCw3ypk8+5YBqFeqI1fLWipZmrez9mw3YAjIgi0KGf/6skTA/rbIXH0rpAoEGXczHgTABwzHTdwue6AECUDyQvD9ZUb/mTvE3XtZaX0CJKa2wJFgMDMZks0hVya78i6JmRqdJuTZiHssPRJj4Yj+olR2jQNatnLAi56b3ftNR4rTT37cA3qnltIPqcpYhVq9pm6OnYZQ1C5WXH+3+82/f3rY4BhJdxIH+91+Qwofvfi1reGo8qJv8YCQBr5vmlRFpsg3WRbDAQKBgQDgPPVa6Nl+Sz3D7vLTh/km/AOB086JgiGclZn+I/Oix2ci0dQddkCo6ldA/VocRQWPb6l2QDngR7VpXd12sn3pOp+dQ/JozRl0ZtFbbUvlhEVBDqa8cuhg5f4Xs0sI7PcL6sNdlhhsa4IL2VbKwHMTGeZKAb6V3t0VUsiefpx9EQKBgQDOpmCvphq2hs44ib6Uusp0A4lj7Oy2XhvCVdb8DIfPlxLaaP56z3I2O8OlM0EeIxlIw890BfbQEXr/DmRcF5Fp97TnCW6+inh7XFZdS7Rf3xxLQozv8HpC5j45ql0CoN2b6A8JdWtLZBHtQtoL4hSOScu8jOGzWxo+tFbkFc3lsQKBgARzkVdVqgOk0LwUAUQLvfl1JUitgLsgFzS5j44I/qwrkzOQwMo4772qsUK8BM7s29hmGhIb4ko+gi5uS5gwoOlBXavMjoJgF/JEnEFZYRlUT9+jv9Gb4lsSFcI4r/OkaJ8W8Pvpn+B/HdzIWr25pk8pw3WSpUrqWq8yroLe9pJRAoGBAKQoXPQLknreuaEGIyQJAlyIlrKPejcgqRKGmCJfXd+VVj+0Wjt64XKdpTxta9Qh86rvCbRULI165WcJqsXRXw173fQOdS/d1fnBD0ZLpjzmgpZyhnzwXl8YgePTJtG4snJwNMnBwfsAwpDko0v/S28AxlzA2WZd/4pCPigv/gDxAoGAFe4jkBcWQZSL/Vg4be9pfxUDBz34eM9NfYpDZjIls8WWghppsDl4933SwZrkzydZ4qeBEVHxOCCrfYGIjnZKta/laFfD1K6S0dcaB7NuoWATAjoxZtsNTo0Iegfi5ruC+GGlV8mJ0bhoPW6ljmAmQijLseNGoXTwK/h/to3ANGo=";
    public static final String RSA_PRIVATE = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YiBaPayConfig.setContext(this);
        final Button showPay = findViewById(R.id.showPay);
        YiBaPayManager.getInstance().setAliOrderInfo(this);
        YiBaPayManager.getInstance().setWxOrderInfo(this);



        showPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YiBaPayManager.getInstance().alipay();
            }
        });
        YiBaPayManager.getInstance().setResultListener(new IResultListener() {
            @Override
            public void onFailed() {
                Toast.makeText(MainActivity.this,"支付失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"支付成功，走不通的逻辑",Toast.LENGTH_LONG).show();
            }
        });

    }



    /**
     * 生成订单信息
     * @return
     */
    @Override
    public String getAlipayInfo() {
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;
        return orderInfo;//"alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017102309473363&biz_content=%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22out_trade_no%22%3A%222017102619271285642%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E6%97%A0%E7%BA%A7%E5%88%AB%E6%99%93%E9%A3%8E%E6%AE%8B%E6%9C%88%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fad.18wifibank.com%2FS32DD%2Falipay%2Fnotify&sign=Auvx9uN6nhnx%2B1etQb1RqANBBvviDtb8MYlOib2F1HvgIrjykK1A4Ce4LfpQ0GwsiF4C7HBc3p33dwRrv321Rmngusz8aEUg13njnTvEB6jBJdXZu0ytkRmpjzpSwqIylM1SOlm1ShZ%2BR%2F24dBPd%2FjfK2W54mTNXfs9voJOb7vB93ytP%2Fvei%2FfhbfXZLMlCfhfH6%2BnzctMuJyHOhhP6LnR2oHj8xUlXAwaEihEZ5G2hmwgM9KE0D4svRXp0Om6JVVOSA8u0s3YKJn4m6Tzl0lMbwOmqTeRuPlbYIT3IoXFdYg8mY0SUbjGaA%2BV4Nt7TmQnrtXQw%2B4xbepZM42hCO1g%3D%3D&sign_type=RSA2&timestamp=2017-10-26+19%3A27%3A12&version=1.0";
    }

    @Override
    public String getWxpayInfo() {
        return "";
    }
}


