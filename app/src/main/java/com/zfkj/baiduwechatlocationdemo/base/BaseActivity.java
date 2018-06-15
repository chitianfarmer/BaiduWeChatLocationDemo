package com.zfkj.baiduwechatlocationdemo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zfkj.baiduwechatlocationdemo.R;

/**
 * 项目名称：baiduwechatlocationdemo
 * 类描述：BaseActivity 描述:基类
 * 创建人：songlijie
 * 创建时间：2018/6/6 17:37
 * 邮箱:814326663@qq.com
 */
public class BaseActivity extends AppCompatActivity {
    public String TAG = this.getClass().getName().toString();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }


    public void back(View view) {
        finish();
    }


    public void setTitle(int title) {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setTitleCenter() {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
        textView.setGravity(Gravity.CENTER);
    }

    public void setTitle(String title) {
        TextView textView = (TextView) this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void hideBackView() {
        TextView iv_back = (TextView) this.findViewById(R.id.iv_back);
        if (iv_back != null) {
            iv_back.setVisibility(View.GONE);
        }
    }

    public void hintTitleBar() {
        RelativeLayout title = (RelativeLayout) this.findViewById(R.id.title);
        if (title != null) {
            title.setVisibility(View.GONE);
        }
    }

    public void showTitleBar() {
        RelativeLayout title = (RelativeLayout) this.findViewById(R.id.title);
        if (title != null) {
            title.setVisibility(View.VISIBLE);
        }
    }

    public void changeBackViewText(int icon) {
        TextView iv_back = (TextView) this.findViewById(R.id.iv_back);
        if (iv_back != null) {
            iv_back.setText(icon);
        }
    }
}
