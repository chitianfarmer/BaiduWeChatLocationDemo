package com.zfkj.baiduwechatlocationdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：LocationBean 描述: 定位所用的对象
 * 创建人：songlijie
 * 创建时间：2018/6/15 15:46
 * 邮箱:814326663@qq.com
 */
public class LocationBean implements Parcelable {
    private String city;
    private double lat;
    private double lng;
    private String name;
    private String address;

    protected LocationBean(Parcel in) {
        city = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        name = in.readString();
    }

    public LocationBean() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
        @Override
        public LocationBean createFromParcel(Parcel in) {
            return new LocationBean(in);
        }

        @Override
        public LocationBean[] newArray(int size) {
            return new LocationBean[size];
        }
    };
}
