package com.cvte.taobaounion.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.taobaounion.R;
import com.cvte.taobaounion.model.domain.HomePagerContent;
import com.cvte.taobaounion.model.domain.SearchResult;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2020/11/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.InnerHolder> {

    private List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> mDatas = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemViwe = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content,parent,false);
        return new InnerHolder(itemViwe);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean mapDataBean = mDatas.get(position);
        holder.setData(mapDataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onItemClick(mapDataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public void setData(SearchResult data) {
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> mResultData = data.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        this.mDatas.clear();
        mDatas.addAll(mResultData);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "SearchAdapter.InnerHolder";

        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_price)
        public TextView offPriceTv;

        @BindView(R.id.goods_after_off_price)
        public TextView finalPriceTv;

        @BindView(R.id.goods_original_price)
        public TextView originalPrice;

        @BindView(R.id.goods_sale_counts)
        public TextView saleCounts;

        public InnerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean mapDataBean) {
            //优惠
            String CouponAmount = mapDataBean.getCoupon_amount();
            title.setText(mapDataBean.getTitle());

            /*调整图片size大小*/
            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            int coverSize = (width>height?width:height)/2;


            Glide.with(itemView.getContext()).load(mapDataBean.getPict_url()).into(cover);
            offPriceTv.setText(CouponAmount);

            String price = mapDataBean.getZk_final_price();
            LogUtils.d(TAG,"price-->" +price);
            if (CouponAmount ==null) {
                CouponAmount = "0";
            }
            float resultPrice = Float.parseFloat(price)- Float.parseFloat(CouponAmount);
            originalPrice.setText(String.format(itemView.getContext().getString(R.string.text_goods_original_price),price));
            //设置中划线
            originalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            finalPriceTv.setText(String.format("%.2f",resultPrice));
            saleCounts.setText(String.format(itemView.getContext().getString(R.string.text_goods_sale_counts),mapDataBean.getVolume()));
        }

    }

    public void setOnListItemClickListener(SearchAdapter.OnListItemClickListener listener){
        this.mOnListItemClickListener = listener;
    }

    public interface OnListItemClickListener{
        void onItemClick(SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean item);
    }
}
