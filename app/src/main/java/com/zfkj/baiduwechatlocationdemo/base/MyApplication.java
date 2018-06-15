package com.zfkj.baiduwechatlocationdemo.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.zfkj.baiduwechatlocationdemo.utils.LoggerUtils;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：MyApplication 描述:
 * 创建人：songlijie
 * 创建时间：2018/6/15 11:39
 * 邮箱:814326663@qq.com
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        LoggerUtils.isDebug(true);
    }
}
