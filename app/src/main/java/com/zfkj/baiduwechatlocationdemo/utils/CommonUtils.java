package com.zfkj.baiduwechatlocationdemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zfkj.baiduwechatlocationdemo.adapter.BaiduSearchPositionAdapter;
import com.zfkj.baiduwechatlocationdemo.adapter.DialogItemAdapter;
import com.zfkj.baiduwechatlocationdemo.adapter.ItemDecorntion;
import com.zfkj.baiduwechatlocationdemo.R;
import com.zfkj.baiduwechatlocationdemo.bean.LocationBean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：CommonUtils 描述:工具类
 * 创建人：songlijie
 * 创建时间：2018/6/6 17:37
 * 邮箱:814326663@qq.com
 */

public class CommonUtils {
    private static Toast toast;
    private static Dialog progressDialog;

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 插入一个字符
     *
     * @param editText
     * @param data
     */
    public static void addChar(EditText editText, String data) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index, data);
    }

    /**
     * 删除一个字符
     *
     * @param editText
     */
    public static void deleteChar(EditText editText) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }

    public static boolean isInLauncher(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (name.equals(className)) {
            return true;
        }
        return false;
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    /**
     * get top activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
            Log.d("observeSoftKeyboard---9", String.valueOf(getSoftButtonsBarHeight(activity)));

        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }

        return softInputHeight;
    }

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (bMute) {
                int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            } else {
                int result = am.abandonAudioFocus(null);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }
        return bool;
    }

    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    //键盘显示监听
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousKeyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - rect.bottom;

                if (Build.VERSION.SDK_INT >= 20) {
                    // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                    keyboardHeight = keyboardHeight - getSoftButtonsBarHeight(activity);

                }

                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide, this);
                }

                previousKeyboardHeight = height;

            }
        });
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);
    }


    public static boolean isChinese(String str) {

        char[] chars = str.toCharArray();
        boolean isGB2312 = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE) {
                    isGB2312 = true;
                    break;
                }
            }
        }
        return isGB2312;
    }

    public static boolean test(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, Object msg) {
        if (context == null) {
            return;
        }
        if (msg instanceof String) {
            showToastShort(context, (String) msg);
        } else if (msg instanceof Integer) {
            showToastShort(context, (int) msg);
        }
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, Object msg) {
        if (context == null) {
            return;
        }
        if (msg instanceof String) {
            showToastLong(context, (String) msg);
        } else if (msg instanceof Integer) {
            showToastLong(context, (int) msg);
        }
    }

    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 发起弹窗
     *
     * @param context
     * @param loadText
     */
    public static void showDialogNumal(Context context, Object loadText) {
        if (context == null) {
            return;
        }
        progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ProgressBar progressBarWaiting = (ProgressBar) progressDialog.findViewById(R.id.iv_loading);
        TextView tv_loading_text = (TextView) progressDialog.findViewById(R.id.tv_loading_text);
        if (loadText instanceof Integer) {
            tv_loading_text.setText(context.getString((int) loadText));
        } else if (loadText instanceof String) {
            tv_loading_text.setText((String) loadText);
        }
        progressDialog.show();
    }

    /**
     * 取消弹窗
     */
    public static void cencelDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    public static void startAppSettings(Context activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    /**
     * 判断是否在底部
     *
     * @param listView
     * @return
     */
    public static boolean isListViewReachBottomEdge(final ListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        return result;
    }

    /**
     * 判断是否是在顶部
     *
     * @param listView
     * @return
     */
    public static boolean isListViewReachTopEdge(final ListView listView) {
        boolean result = false;
        if (listView.getFirstVisiblePosition() == 0) {
            final View topChildView = listView.getChildAt(0);
            result = topChildView.getTop() == 0;
        }
        return result;
    }


    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = "127.0.0.1";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 强制弹起键盘
     *
     * @param context
     * @param editText
     */
    public static void showSoftInput(Context context, View editText) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);//强制显示键盘
    }

    /**
     * 强制隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hintSoftInput(Context context, View view) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }


    /**
     * 保存图片到相册
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean saveImageToAlubm(Context context, String filePath) {
        if (context == null) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "demo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        String fileName = "demo_" + System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//        } catch (Exception e) {
//            return false;
//        }

        //通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return true;
    }


    /**
     * 地图搜索弹窗
     *
     * @param context
     * @param listener
     * @param bean
     */
    public static void showSearchPup(final Context context, final LocationBean bean, final OnPoiSearchItemClickListener listener) {
        if (context == null || bean == null || listener == null) {
            return;
        }
        final List<LocationBean> datas = new ArrayList<>();
        /**
         *实例化
         */
        final PoiSearch mPoiSearch = PoiSearch.newInstance();
        //获得pup的view
        View view = LayoutInflater.from(context).inflate(R.layout.layout_map_search, null, false);
        final EditText edt_search = (EditText) view.findViewById(R.id.edt_search);
        final TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        final TextView tv_close = (TextView) view.findViewById(R.id.tv_close);
        final RecyclerView recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerview.addItemDecoration(new ItemDecorntion(0, 1, 0, 1));
        final BaiduSearchPositionAdapter locatorAdapter = new BaiduSearchPositionAdapter(context, datas);
        recyclerview.setAdapter(locatorAdapter);
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //设置window的宽高   1 window的布局 2、window的宽  3、window的高  4、window是否获取焦点
//        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, screenHeight, true);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //设置window背景色
        window.setBackgroundDrawable(new ColorDrawable(00000000));
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        window.setFocusable(true);
        //设置键盘不遮盖
        showSoftInput(context, edt_search);
        window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置window动画
        window.setAnimationStyle(R.style.Anim_style);
        //设置window在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        recyclerview.setVisibility(View.GONE);
        tv_close.setVisibility(View.VISIBLE);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // 根据输入框的内容，进行搜索
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(bean.getCity())//城市
                            .keyword(s.toString())//检索关键字
                            .pageNum(0)//分页编码
                            .pageCapacity(50));//每页容量，默认10条
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error != PoiResult.ERRORNO.NO_ERROR) {
                    recyclerview.setBackgroundResource(R.color.transparent);
                    recyclerview.setVisibility(View.GONE);
                    tv_close.setVisibility(View.VISIBLE);
                    showToastShort(context, R.string.nothing_to_search);
                    return;
                }
                if (poiResult == null || poiResult.getAllPoi() == null) {
                    recyclerview.setBackgroundResource(R.color.transparent);
                    recyclerview.setVisibility(View.GONE);
                    tv_close.setVisibility(View.VISIBLE);
                    showToastShort(context, R.string.nothing_to_search);
                    return;
                }
                recyclerview.setVisibility(View.VISIBLE);
                tv_close.setVisibility(View.GONE);
                recyclerview.setBackgroundResource(R.color.wight_grey);
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                //获取在线建议检索结果
                if (datas != null) {
                    datas.clear();
                    for (int i = 0; i < allPoi.size(); i++) {
                        PoiInfo info = allPoi.get(i);
                        if (info.location != null && !TextUtils.isEmpty(info.address) && !TextUtils.isEmpty(info.city)) {
                            LocationBean bean = new LocationBean();
                            bean.setLng(info.location.longitude);
                            bean.setLat(info.location.latitude);
                            bean.setAddress(info.address);
                            bean.setCity(info.city);
                            bean.setName(info.name);
                            if (!datas.contains(bean)) {
                                datas.add(bean);
                            }
                        }
                    }
                    locatorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        locatorAdapter.setClickListener(new BaiduSearchPositionAdapter.OnItemClickListener() {
            @Override
            public void onItemCLicked(int position, LocationBean info) {
                window.dismiss();
                locatorAdapter.setSelectSearchItemIndex(position);
                locatorAdapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick(position, info);
                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                mPoiSearch.destroy();
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                mPoiSearch.destroy();
            }
        });
    }

    public interface OnPoiSearchItemClickListener {
        void onItemClick(int position, LocationBean bean);
    }


    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            verName = "";
        }
        return verName;
    }

    /**
     * 显示导航地图选择的dialog
     *
     * @param context
     * @param title
     */
    public static void showNavigationDialog(Context context, String title, List<String> items, final OnItemClickListener listener) {
        if (context == null) {
            return;
        }
        if (items == null) {
            items = new ArrayList<>();
            items.add(context.getString(R.string.bd_map));
            items.add(context.getString(R.string.gd_map));
            items.add(context.getString(R.string.tc_map));
            items.add(context.getString(R.string.gg_map));
            items.add(context.getString(R.string.cancle));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.layout_item_dialog, null);
        LinearLayout ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        RecyclerView recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        if (TextUtils.isEmpty(title)) {
            ll_title.setVisibility(View.GONE);
            tv_title.setText("");
        } else {
            ll_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        recyclerview.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerview.addItemDecoration(new ItemDecorntion(5, 5 / 2, 5, 5 / 2));
        DialogItemAdapter adapter = new DialogItemAdapter(context, items);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new DialogItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, String msg) {
                dialog.dismiss();
                listener.onClick(position, msg);
            }
        });
        dialog.show();
    }

    public interface OnItemClickListener {
        void onClick(int position, String msg);
    }
}
