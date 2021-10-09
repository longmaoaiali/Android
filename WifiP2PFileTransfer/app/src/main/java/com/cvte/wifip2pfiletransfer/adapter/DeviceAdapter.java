package com.cvte.wifip2pfiletransfer.adapter;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvte.wifip2pfiletransfer.MainActivity;
import com.cvte.wifip2pfiletransfer.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by user on 2020/11/3.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<WifiP2pDevice> mWifiP2pDeviceList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);

        //todo:设置item的点击事件

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTv_deviceName.setText(mWifiP2pDeviceList.get(position).deviceName);
        holder.mTv_deviceAddress.setText(mWifiP2pDeviceList.get(position).deviceAddress);
        /** Device connection status */
        holder.mTv_deviceDetails.setText(MainActivity.getDeviceStatus(mWifiP2pDeviceList.get(position).status));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mWifiP2pDeviceList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWifiP2pDeviceList.size();
    }

    public void setData(List<WifiP2pDevice> wifiP2pDeviceList) {
        this.mWifiP2pDeviceList = wifiP2pDeviceList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTv_deviceName;
        private final TextView mTv_deviceAddress;
        private final TextView mTv_deviceDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            mTv_deviceName = itemView.findViewById(R.id.tv_deviceName);
            mTv_deviceAddress = itemView.findViewById(R.id.tv_deviceAddress);
            mTv_deviceDetails = itemView.findViewById(R.id.tv_deviceDetails);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(WifiP2pDevice wifiP2pDevice);
    }
}
