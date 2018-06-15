package com.zfkj.baiduwechatlocationdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.zfkj.baiduwechatlocationdemo.R;
import com.zfkj.baiduwechatlocationdemo.base.BaseActivity;
import com.zfkj.baiduwechatlocationdemo.bean.LocationBean;
import com.zfkj.baiduwechatlocationdemo.utils.CommonUtils;
import com.zfkj.baiduwechatlocationdemo.utils.LoggerUtils;
import com.zfkj.baiduwechatlocationdemo.utils.MapUtils;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：BaiduMapNativeActivity 描述:
 * 创建人：songlijie
 * 创建时间：2018/6/6 17:37
 * 邮箱:814326663@qq.com
 */
public class BaiduMapNativeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_name, tv_address;
    private ImageView iv_nagivation, iv_re_location;
    private MapView mapview;
    private BaiduMap mBaiduMap;
    private BaiduSDKReceiver mBaiduReceiver;
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * 定位
     */
    private LocationClient mLocClient;
    /**
     * 传递过来的数据
     */
    private double latitude, longtitude;
    private String address;
    private double fromLat, fromLng;
    private String fromAddress;
    /**
     * 当前经纬度
     */
    private LatLng mLoactionLatLng;
    private LocationBean locationBean = null;


    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            String st2 = getResources().getString(R.string.please_check);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                CommonUtils.showToastShort(context, st2);
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                CommonUtils.showToastShort(context, st1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_native);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
        locationBean = getIntent().getParcelableExtra("Location");
        if (locationBean == null) {
            finish();
            return;

        }
        latitude = locationBean.getLat();
        longtitude = locationBean.getLng();
        address = locationBean.getAddress();
        if (latitude == 0 || longtitude == 0 || TextUtils.isEmpty(address)) {
            finish();
            return;
        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        iv_nagivation = (ImageView) findViewById(R.id.iv_navigation);
        iv_re_location = (ImageView) findViewById(R.id.iv_re_location);
        mapview = (MapView) findViewById(R.id.mapview);
    }

    private void initData() {
        tv_title.setText(R.string.location_message);
        mBaiduMap = mapview.getMap();
        // 设置为普通矢量图地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mapview.setPadding(10, 0, 0, 10);
        View child = mapview.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        mapview.showScaleControl(false);
        // 不显示地图缩放控件（按钮控制栏）
        mapview.showZoomControls(false);
        // 设置缩放比例(500米)
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        // 可定位
        mBaiduMap.setMyLocationEnabled(true);
        showMap(latitude, longtitude);
        String name = locationBean.getName();
        tv_name.setText(TextUtils.isEmpty(name) || getString(R.string.now_address).equals(name) ? address : name);
        tv_address.setText(address);
    }

    private void showMap(final double latitude, final double longtitude) {
        LatLng llA = new LatLng(latitude, longtitude);
        mLoactionLatLng = llA;
        addMarker(llA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llA);
        mBaiduMap.animateMapStatus(u);
    }


    private void setListener() {
        iv_re_location.setOnClickListener(this);
        iv_nagivation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_navigation://导航
                startLocation();
                showCityPup(String.valueOf(fromLat), String.valueOf(fromLng), fromAddress, String.valueOf(latitude), String.valueOf(longtitude), address);
                break;
            case R.id.iv_re_location://重新定位
//                startLocation();
                if (mLoactionLatLng != null) {
                    // 实现动画跳转
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLoactionLatLng);
                    addMarker(mLoactionLatLng);
                    mBaiduMap.animateMapStatus(u);
                }
                break;
        }
    }

    private void showCityPup(final String fromLat, final String fromLng, final String fromAddress, final String activityLat, final String activityLng, final String address) {
        CommonUtils.showNavigationDialog(BaiduMapNativeActivity.this, getString(R.string.navigation), null, new CommonUtils.OnItemClickListener() {
            @Override
            public void onClick(int position, String msg) {
                switch (position) {
                    case 0:
                        MapUtils.openBaiduMap(BaiduMapNativeActivity.this, activityLat, activityLng, address);
                        break;
                    case 1:
                        MapUtils.openGDMap(BaiduMapNativeActivity.this, activityLat, activityLng, address);
                        break;
                    case 2:
                        MapUtils.openTencentMap(BaiduMapNativeActivity.this, fromAddress, fromLat, fromLng, activityLat, activityLng, address);
                        break;
                    case 3:
                        MapUtils.openGoogleMap(BaiduMapNativeActivity.this, fromLat, fromLng, activityLat, activityLng);
                        break;
                    case 4:

                        break;
                    default:

                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        mapview.onPause();
        stopLocation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapview.onResume();
        startLocation();
        super.onResume();
    }

    private void startLocation() {
        if (mLocClient != null) {
            mLocClient.start();
        }
    }

    private void stopLocation() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        stopLocation();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mapview.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }

    /**
     * format new location to string and show on screen
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapview == null) {
                CommonUtils.showToastShort(getBaseContext(), R.string.location_failed);
                return;
            }
            fromLat = location.getLatitude();
            fromLng = location.getLongitude();
            fromAddress = location.getAddrStr();
            LoggerUtils.e("------定位成功:" + fromAddress);
//            stopLocation();
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    private void addMarker(LatLng latLng) {
        mBaiduMap.clear();
        BitmapDescriptor resource = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(resource)
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
    }
}
