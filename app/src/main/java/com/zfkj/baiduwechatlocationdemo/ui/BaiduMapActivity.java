package com.zfkj.baiduwechatlocationdemo.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zfkj.baiduwechatlocationdemo.R;
import com.zfkj.baiduwechatlocationdemo.adapter.ItemDecorntion;
import com.zfkj.baiduwechatlocationdemo.adapter.LocationAdapter;
import com.zfkj.baiduwechatlocationdemo.base.BaseActivity;
import com.zfkj.baiduwechatlocationdemo.bean.LocationBean;
import com.zfkj.baiduwechatlocationdemo.utils.CommonUtils;
import com.zfkj.baiduwechatlocationdemo.utils.LoggerUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：BaiduMapActivity 描述:百度地图页面
 * 创建人：songlijie
 * 创建时间：2018/6/6 17:37
 * 邮箱:814326663@qq.com
 */
public class BaiduMapActivity extends BaseActivity implements OnGetGeoCoderResultListener, LocationAdapter.OnItemClickListener, BaiduMap.OnMapStatusChangeListener, View.OnClickListener, BaiduMap.OnMapTouchListener {

    static MapView mMapView = null;
    private ProgressBar progress_bar;
    public MyLocationListenner myListener = new MyLocationListenner();
    TextView sendButton = null;
    static LocationBean lastLocation = null;
    private TextView tv_search;
    private BaiduSDKReceiver mBaiduReceiver;
    private RelativeLayout rl_search;

    /**
     * 列表适配器
     */
    private LocationAdapter locatorAdapter;
    /**
     * 附近地点列表
     */
    private RecyclerView recyclerview;
    /**
     * 列表数据
     */
    private List<LocationBean> datas = new ArrayList<>();
    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;
    /**
     * 地理编码
     */
    private GeoCoder mSearch;
    /**
     * 定位
     */
    private LocationClient mLocClient;
    // MapView 中央对于的屏幕坐标
    private android.graphics.Point mCenterPoint = null;
    /**
     * 当前经纬度
     */
    private LatLng mLoactionLatLng;
    /**
     * 是否第一次定位
     */
    private boolean isFirstLoc = true;
    /**
     * 按钮：回到原地
     */
    private ImageView iv_re_location;
    private boolean isTouch = true;
    private GridLayoutManager layoutManager;

    public class BaiduSDKReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            String st2 = getResources().getString(R.string.please_check);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                CommonUtils.showToastShort(context, st2);
                Toast.makeText(context, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                CommonUtils.showToastShort(context, st1);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize SDK with context, should call this before setContentView
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidumap);
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
        lastLocation = new LocationBean();
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        sendButton = (TextView) findViewById(R.id.btn_rtc);
        iv_re_location = (ImageView) findViewById(R.id.iv_re_location);
        tv_search = (TextView) findViewById(R.id.tv_search);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
    }

    private void initData() {
        setTitle(R.string.location_message);
        sendButton.setText(R.string.button_send);

        layoutManager = new GridLayoutManager(getBaseContext(), 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new ItemDecorntion(0, 1, 0, 1));
        sendButton.setVisibility(View.VISIBLE);
        initMap();
        // 列表初始化
        locatorAdapter = new LocationAdapter(this, datas);
        recyclerview.setAdapter(locatorAdapter);
    }

    private void setListener() {
        mMapView.setLongClickable(true);
        iv_re_location.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        locatorAdapter.setClickListener(this);
        rl_search.setOnClickListener(this);
        sendButton.setOnClickListener(this);
    }

    private void initMap() {
        CommonUtils.showDialogNumal(BaiduMapActivity.this, R.string.locationing);
        mBaiduMap = mMapView.getMap();
        // 设置为普通矢量图地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        mMapView.showScaleControl(false);
        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);
        // 设置缩放比例(500米)
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(this);
        // 初始化当前 MapView 中心屏幕坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;
        // 地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 地图状态监听
        mBaiduMap.setOnMapStatusChangeListener(this);
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
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        stopLocation();
        super.onPause();
        lastLocation = null;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
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
        mMapView.onDestroy();
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
            if (location == null || mMapView == null) {
                return;
            }
            CommonUtils.cencelDialog();
            if (lastLocation != null) {
                if (lastLocation.getLat() == location.getLatitude() && lastLocation.getLng() == location.getLongitude()) {
                    // mMapView.refresh(); //need this refresh?
                    return;
                }
            }
            LoggerUtils.e("---定位成功:" + location.getAddrStr());
            lastLocation.setAddress(location.getAddrStr());
            lastLocation.setCity(location.getCity());
            lastLocation.setName(location.getBuildingName());
            lastLocation.setLat(location.getLatitude());
            lastLocation.setLng(location.getLongitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            Double mLatitude = location.getLatitude();
            Double mLongitude = location.getLongitude();
            LatLng currentLatLng = new LatLng(mLatitude, mLongitude);
            mLoactionLatLng = new LatLng(mLatitude, mLongitude);
            stopLocation();
            // 是否第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                // 实现动画跳转
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);
                mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
                return;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }


    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        CommonUtils.showDialogNumal(BaiduMapActivity.this, R.string.are_doing);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int hight = point.y;
        Rect rect = new Rect(0, (hight / 2) - (width / 4) - 180, width, (hight / 2) + (width / 4) - 180);
        mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/location/");
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File file = new File(file1.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                    BaiduMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    Intent intent = BaiduMapActivity.this.getIntent();
                    intent.putExtra("Location", lastLocation);
                    intent.putExtra("thumbnailPath", file.getAbsolutePath());
                    BaiduMapActivity.this.setResult(RESULT_OK, intent);
                    finish();
                } catch (FileNotFoundException e) {
                    BaiduMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    BaiduMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.cencelDialog();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_re_location://重定位
                isTouch = true;
                isFirstLoc = true;
                initMap();
                startLocation();
                break;
            case R.id.tv_search:
            case R.id.rl_search:
                CommonUtils.showSearchPup(BaiduMapActivity.this, lastLocation, new CommonUtils.OnPoiSearchItemClickListener() {
                    @Override
                    public void onItemClick(int position, LocationBean bean) {
                        locatorAdapter.setSelectSearchItemIndex(0);
                        locatorAdapter.notifyDataSetChanged();
                        // 获取经纬度
                        LatLng latLng = new LatLng(bean.getLat(), bean.getLng());
                        // 实现动画跳转
                        MapStatusUpdate u = MapStatusUpdateFactory
                                .newLatLng(latLng);
                        mBaiduMap.animateMapStatus(u);
                        mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                                .location(latLng));
                    }
                });
                break;
            case R.id.btn_rtc:
                sendLocation(v);
                break;
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        // 正向地理编码指的是由地址信息转换为坐标点的过程
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
    }


    @Override
    public void onItemCLicked(int position, LocationBean info) {
        isTouch = false;
        lastLocation = info;
        locatorAdapter.setSelectSearchItemIndex(position);
        locatorAdapter.notifyDataSetChanged();
        mBaiduMap.clear();
        LatLng latLng = new LatLng(info.getLat(), info.getLng());
        addMarker(latLng);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(latLng);
        mBaiduMap.animateMapStatus(u);
        mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                .location(latLng));
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        if (!isTouch) {
            return;
        }
        String address = result.getAddress();
        // 获取反向地理编码结果
        if (result.getLocation() != null && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(result.getAddressDetail().city)) {
            if (lastLocation == null) {
                lastLocation = new LocationBean();
            }
            lastLocation.setAddress(address);
            String city = result.getAddressDetail().city;
            lastLocation.setLat(result.getLocation().latitude);
            lastLocation.setLng(result.getLocation().longitude);
            lastLocation.setCity(city);
            lastLocation.setName(getString(R.string.now_address));
        }

        datas.clear();
        if (!TextUtils.isEmpty(address)) {
            datas.add(lastLocation);
        }
        List<PoiInfo> poiList = result.getPoiList();
        if (poiList != null && poiList.size() > 0) {
            for (int i = 0; i < poiList.size(); i++) {
                PoiInfo info = poiList.get(i);
                if (info.location != null && !TextUtils.isEmpty(info.address) && !TextUtils.isEmpty(info.city)) {
                    LocationBean location = new LocationBean();
                    location.setAddress(info.address);
                    location.setLat(info.location.latitude);
                    location.setLng(info.location.longitude);
                    location.setCity(info.city);
                    location.setName(info.name);
                    if (!datas.contains(location)) {
                        datas.add(location);
                    }
                }
            }
        }
        locatorAdapter.notifyDataSetChanged();
        progress_bar.setVisibility(View.GONE);
    }

    /**
     * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
     *
     * @param status 地图状态改变开始时的地图状态
     */
    public void onMapStatusChangeStart(MapStatus status) {

    }

    /**
     * 地图状态变化中
     *
     * @param status 当前地图状态
     */
    public void onMapStatusChange(MapStatus status) {
        if (isTouch) {
            datas.clear();
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(status.target));
            locatorAdapter.setSelectSearchItemIndex(0);
            locatorAdapter.notifyDataSetChanged();
            addMarker(status.target);
        }
    }

    /**
     * 地图状态改变结束
     *
     * @param status 地图状态改变结束后的地图状态
     */
    public void onMapStatusChangeFinish(MapStatus status) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        isTouch = true;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // 显示列表，查找附近的地点
            searchPoi();
        }
    }

    /**
     * 显示列表，查找附近的地点
     */
    public void searchPoi() {
        if (mCenterPoint == null) {
            return;
        }
        // 获取当前 MapView 中心屏幕坐标对应的地理坐标
        LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
        // 发起反地理编码检索
        mSearch.reverseGeoCode((new ReverseGeoCodeOption())
                .location(currentLatLng));
        progress_bar.setVisibility(View.VISIBLE);
    }


    private void addMarker(LatLng latLng) {
        mBaiduMap.clear();
        BitmapDescriptor resource = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
        OverlayOptions ooA = new MarkerOptions().position(latLng).icon(resource)
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
    }
}
