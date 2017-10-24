package demo.alipay.yiba.com.ybpaydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yiba.pay.YiBaPayConfig;
import com.yiba.pay.YiBaPayManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YiBaPayConfig.setContext(this);
        final Button showPay = findViewById(R.id.showPay);
        showPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YiBaPayManager.getInstance().show(showPay);
            }
        });

    }
}
