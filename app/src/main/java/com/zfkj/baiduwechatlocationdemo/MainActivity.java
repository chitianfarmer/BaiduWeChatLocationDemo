package com.zfkj.baiduwechatlocationdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zfkj.baiduwechatlocationdemo.base.BaseActivity;
import com.zfkj.baiduwechatlocationdemo.bean.LocationBean;
import com.zfkj.baiduwechatlocationdemo.runtimepermissions.PermissionsManager;
import com.zfkj.baiduwechatlocationdemo.runtimepermissions.PermissionsResultAction;
import com.zfkj.baiduwechatlocationdemo.ui.BaiduMapActivity;
import com.zfkj.baiduwechatlocationdemo.ui.BaiduMapNativeActivity;
import com.zfkj.baiduwechatlocationdemo.utils.CommonUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_LOCAL = 3;
    private Button btn_open;
    private ImageView iv_show;
    private TextView tv_show;
    private LocationBean locationBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        initListener();
    }

    private void initView() {
        hideBackView();
        btn_open = (Button) findViewById(R.id.btn_open);
        tv_show = (TextView) findViewById(R.id.tv_show);
        iv_show = (ImageView) findViewById(R.id.iv_show);
    }

    private void initListener() {
        btn_open.setOnClickListener(this);
        tv_show.setOnClickListener(this);
        iv_show.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                if (!checkPermission()) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.permission_error);
                    return;
                }
                tv_show.setText("");
                iv_show.setImageResource(R.mipmap.ic_launcher);
                locationBean = null;
                startActivityForResult(new Intent(MainActivity.this, BaiduMapActivity.class), REQUEST_CODE_LOCAL);
                break;
            case R.id.tv_show:
            case R.id.iv_show:
                if (locationBean == null) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.permission_need);
                    return;
                }
                if (!checkPermission()) {
                    CommonUtils.showToastShort(getBaseContext(), R.string.permission_error);
                    return;
                }
                Intent intent = new Intent(MainActivity.this, BaiduMapNativeActivity.class);
                intent.putExtra("Location", locationBean);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOCAL:
                    if (data != null) {
                        LocationBean location = data.getParcelableExtra("Location");
                        locationBean = location;
                        String thumbnailPath = data.getStringExtra("thumbnailPath");
                        tv_show.setText("经度:" + locationBean.getLat() + "\n维度:" + locationBean.getLng() + "\n位置:" + locationBean.getAddress() + "\n城市:" + locationBean.getCity() + "\n名字:" + locationBean.getName());
                        Glide.with(MainActivity.this).load(thumbnailPath).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_show);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            // 没有获取到权限，做特殊处理
            return false;
        } else {
            return true;
        }
    }
}
