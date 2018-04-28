package demo.alipay.yiba.com.ybpaydemo;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by yh on 2017/10/23.
 */

public class PayWindow extends PopupWindow {

    private onPayListener listener;

    public PayWindow(Context context){
        View rootView = LayoutInflater.from(context).inflate(com.yiba.pay.R.layout.popupwindow_pay,null);
        setContentView(rootView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        rootView.findViewById(com.yiba.pay.R.id.ali_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.aliPay();
                }
            }
        });
        rootView.findViewById(com.yiba.pay.R.id.wx_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.wxPay();
                }
            }
        });
        rootView.findViewById(com.yiba.pay.R.id.stripe_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!= null){
                    listener.stripePay();
                }
            }
        });

    }

    public void showAtLocation(View parent) {
        super.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public void setPayListener(onPayListener listener){
        this.listener = listener;
    }
    public interface onPayListener{
        void aliPay();
        void wxPay();
        void stripePay();
    }

}
