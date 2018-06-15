package com.zfkj.baiduwechatlocationdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zfkj.baiduwechatlocationdemo.R;
import com.zfkj.baiduwechatlocationdemo.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 项目名称：baiduwechatlocationdemo
 * 类描述：LocationAdapter 描述:poi地点列表适配器(地图页面)
 * 创建人：songlijie
 * 创建时间：2018/6/6 17:37
 * 邮箱:814326663@qq.com
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private Context context;
    private List<LocationBean> datas = new ArrayList<>();
    private OnItemClickListener clickListener;

    /**
     * 选中的item下标
     */
    private int selectItemIndex;

    public LocationAdapter(Context context, List<LocationBean> datas) {
        this.datas = datas;
        this.context = context;
        // 默认第一个为选中项
        selectItemIndex = 0;
    }

    public void setSelectSearchItemIndex(int selectItemIndex) {
        this.selectItemIndex = selectItemIndex;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.location_item_poi, null);
        LocationViewHolder viewHolder = new LocationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, final int position) {
        final LocationBean info = datas.get(position);
        holder.tv_poi_address.setText(info.getAddress());
        holder.tv_poi_name.setText(info.getName());
        if (selectItemIndex == position) {
            holder.img_cur_point.setImageResource(R.mipmap.position_is_select);
        } else {
            holder.img_cur_point.setImageDrawable(null);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemCLicked(position, info);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        /**
         * 名称
         */
        private TextView tv_poi_name;
        /**
         * 地址
         */
        private TextView tv_poi_address;
        /**
         * 选中的按钮
         */
        private ImageView img_cur_point;

        public LocationViewHolder(View itemView) {
            super(itemView);
            tv_poi_name = (TextView) itemView.findViewById(R.id.tv_poi_name);
            tv_poi_address = (TextView) itemView.findViewById(R.id.tv_poi_address);
            img_cur_point = (ImageView) itemView.findViewById(R.id.img_cur_point);
        }
    }

    public interface OnItemClickListener {
        void onItemCLicked(int position, LocationBean info);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
