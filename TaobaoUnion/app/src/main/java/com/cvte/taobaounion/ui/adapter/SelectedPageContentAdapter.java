package com.cvte.taobaounion.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.taobaounion.R;
import com.cvte.taobaounion.model.domain.SelectedContentNew;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;
import com.cvte.taobaounion.utils.Constant;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2020/11/9.
 */

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {

    private static final String TAG = "SelectedPageContentAdapter";
    private List<SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData =
            new ArrayList<>();
    private OnSelectedPageContentClickListener mOnSelectedPageContentClickListener = null;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_right,null,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        //todo:
        SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean = mData.get(position);
        holder.setData(mapDataBean);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectedPageContentClickListener != null) {
                    mOnSelectedPageContentClickListener.onContentItemClick(mapDataBean);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContentNew content) {
        if (content.getCode()  == Constant.SUCCESS_CODE) {
           List<SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> map_data = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(map_data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.select_off_price)
        public TextView selectOffPrice;

        @BindView(R.id.select_goods_title)
        public TextView selectGooodsTitle;

        @BindView(R.id.select_buy_btn)
        public TextView selectBuyBtn;

        @BindView(R.id.select_original_price)
        public TextView selectOriginalPrice;

        public InnerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


        public void setData(SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean) {
            LogUtils.d(TAG,"mapDataBean -->  "+mapDataBean.getClick_url());
            LogUtils.d(TAG,"mapDataBean -->  "+mapDataBean.getTitle());
            LogUtils.d(TAG,"mapDataBean -->  "+mapDataBean.getShop_title());
            //商品名 优惠价 原价 图片 店家按钮
            selectGooodsTitle.setText(mapDataBean.getTitle());

            String picUrl = UrlUtils.getCoverPath(mapDataBean.getPict_url());
            LogUtils.d(TAG,"mapDataBean picture url-->  "+picUrl);
            Glide.with(itemView.getContext()).load(picUrl).into(cover);

            if (TextUtils.isEmpty(mapDataBean.getCoupon_click_url())) {
                selectOriginalPrice.setText("晚了，优惠券没了");
                selectBuyBtn.setVisibility(View.GONE);
            } else {
                selectBuyBtn.setVisibility(View.VISIBLE);
                selectOriginalPrice.setText("原价"+mapDataBean.getZk_final_price());
            }

            if (TextUtils.isEmpty(mapDataBean.getCoupon_info())) {
                selectOffPrice.setVisibility(View.GONE);
            } else {
                selectOffPrice.setVisibility(View.VISIBLE);
                selectOffPrice.setText(mapDataBean.getCoupon_info());
            }

        }
    }

    public void setOnSelectedPageContentClickListener(OnSelectedPageContentClickListener listener){
        this.mOnSelectedPageContentClickListener = listener;
    }

    public interface  OnSelectedPageContentClickListener{
        void onContentItemClick(SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item);
    }


}
