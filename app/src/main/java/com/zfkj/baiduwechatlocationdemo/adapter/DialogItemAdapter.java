package com.zfkj.baiduwechatlocationdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zfkj.baiduwechatlocationdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：BaiduWeChatLocationDemo
 * 类描述：DialogItemAdapter 描述:导航弹窗的适配器
 * 创建人：songlijie
 * 创建时间：2018/6/15 11:21
 * 邮箱:814326663@qq.com
 */
public class DialogItemAdapter extends RecyclerView.Adapter<DialogItemAdapter.DialogItemHolder> {
    private Context mContext;
    private List<String> objectList = new ArrayList<>();
    private OnItemClickListener listener;

    public DialogItemAdapter(Context mContext, List<String> objectList) {
        this.mContext = mContext;
        this.objectList = objectList;
    }

    @Override
    public DialogItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_navation, null);
        DialogItemHolder holder = new DialogItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DialogItemHolder holder, final int position) {
        final String item = objectList.get(position);
        holder.tv_item.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(position, item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class DialogItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_item;

        public DialogItemHolder(View itemView) {
            super(itemView);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position, String msg);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.listener = clickListener;
    }
}
