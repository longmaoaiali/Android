package com.cvte.taobaounion.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.taobaounion.R;
import com.cvte.taobaounion.model.domain.SellContent;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2020/11/10.
 */

public class SellContentAdapter extends RecyclerView.Adapter<SellContentAdapter.InnerHolder> {


    private List<SellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mMapData = new ArrayList<>();
    private static final String TAG = "SellContentAdapter";
    private OnSellPageItemClickListener mOnSellPageItemClickListener;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sell_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        SellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean = mMapData.get(position);
        holder.setData(mapDataBean);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSellPageItemClickListener != null) {
                    mOnSellPageItemClickListener.onSellItemClick(mapDataBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMapData.size();
    }

    public void setData(SellContent result) {
        this.mMapData.clear();
        LogUtils.d(TAG,"sell result-->"+result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size());
        this.mMapData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sell_cover)
        public ImageView sellCover;

        @BindView(R.id.sell_title)
        public TextView titletv;

        @BindView(R.id.sell_off_price)
        public TextView offPrice;

        @BindView(R.id.sell_origin_prise)
        public TextView originPrise;


        public InnerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(SellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean) {
            titletv.setText(mapDataBean.getTitle());
            String coverPath = UrlUtils.getCoverPath(mapDataBean.getPict_url());
            Glide.with(itemView.getContext()).load(coverPath).into(sellCover);

            String originalPrise = mapDataBean.getZk_final_price();
            originPrise.setText("￥"+originalPrise);
            originPrise.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            int couponAmount = mapDataBean.getCoupon_amount();
            float originalPriseParse = Float.parseFloat(originalPrise);
            float finalPrise = originalPriseParse - couponAmount;
            offPrice.setText("券后价："+String.format("%.2f",finalPrise));
        }
    }

    public void setOnSellPageItemClickListener(OnSellPageItemClickListener listener) {
        this.mOnSellPageItemClickListener = listener;
    }

    public interface OnSellPageItemClickListener {
        void onSellItemClick(SellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean mapDataBean);
    }
}
