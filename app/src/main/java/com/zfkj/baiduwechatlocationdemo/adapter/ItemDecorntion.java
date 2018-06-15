package com.zfkj.baiduwechatlocationdemo.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 项目名称：baiduwechatlocationdemo
 * 类描述：ItemDecorntion 描述:recyclerview的自定义偏移量即条目
 * 创建人：songlijie
 * 创建时间：2018/1/5 13:15
 * 邮箱:814326663@qq.com
 */
public class ItemDecorntion extends RecyclerView.ItemDecoration {
    private int top,right,left,bottom;

    public ItemDecorntion(int left, int top, int right, int bottom) {
        this.top = top;
        this.right = right;
        this.left = left;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(left,top,right,bottom);
    }
}
