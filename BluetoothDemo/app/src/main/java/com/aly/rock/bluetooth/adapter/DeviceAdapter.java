package com.aly.rock.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.aly.rock.bluetooth.R;

public class DeviceAdapter extends BaseAdapter {

    private List<BluetoothDevice> bluetoothDeviceList;

    private LayoutInflater layoutInflater;
    private static ViewHolder currentViewHolder = null;
    public DeviceAdapter(Context context) {
        bluetoothDeviceList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!bluetoothDeviceList.contains(device)) {
            bluetoothDeviceList.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return bluetoothDeviceList.get(position);
    }

    public void clear() {
        bluetoothDeviceList.clear();
    }

    @Override
    public int getCount() {
        return bluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int i) {
        return bluetoothDeviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_deviceStatus = view.findViewById(R.id.tv_deviceStatus);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        currentViewHolder = viewHolder;
        BluetoothDevice bluetoothDevice = bluetoothDeviceList.get(i);
        StringBuilder sb = new StringBuilder();
        sb.append("设备名：");
        sb.append(TextUtils.isEmpty(bluetoothDevice.getName()) ? "未知" : bluetoothDevice.getName());
        sb.append("\nMac地址：");
        sb.append(bluetoothDevice.getAddress());
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            sb.append("\n已配对");
        }
        viewHolder.tv_deviceStatus.setText(sb);
        return view;
    }
    public ViewHolder getCurrentviewHolder(){
        return currentViewHolder;
    }
    public static class ViewHolder {
        public TextView tv_deviceStatus;
    }

}