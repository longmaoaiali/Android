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
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2020/10/27.
 */

public class HomepageContentAdapter extends RecyclerView.Adapter<HomepageContentAdapter.InnerHolder> {
    List<HomePagerContent.DataBean> data = new ArrayList<>();
    private OnListItemClickListener mOnListItemClickListener = null;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        HomePagerContent.DataBean dataBean = data.get(position);
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnListItemClickListener != null) {
                    HomePagerContent.DataBean item = data.get(position);
                    mOnListItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "HomepageContentAdapter.InnerHolder";

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

        public void setData(HomePagerContent.DataBean dataBean) {
            long CouponAmount = dataBean.getCoupon_amount();
            title.setText(dataBean.getTitle());

            /*调整图片size大小*/
            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            int coverSize = (width>height?width:height)/2;


            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(dataBean.getPict_url(),coverSize)).into(cover);
            offPriceTv.setText(String.format(itemView.getContext().getString(R.string.text_goods_off_price),CouponAmount));

            String price = dataBean.getZk_final_price();
            LogUtils.d(TAG,"price-->" +price);
            float resultPrice = Float.parseFloat(price)-CouponAmount;
            originalPrice.setText(String.format(itemView.getContext().getString(R.string.text_goods_original_price),price));
            originalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            finalPriceTv.setText(String.format("%.2f",resultPrice));
            saleCounts.setText(String.format(itemView.getContext().getString(R.string.text_goods_sale_counts),dataBean.getVolume()));
        }
    }
    
    
    public void setOnListItemClickListener(OnListItemClickListener listener){
        this.mOnListItemClickListener = listener;
    }
    
    public interface OnListItemClickListener{
        void onItemClick(HomePagerContent.DataBean item);
    }
}
