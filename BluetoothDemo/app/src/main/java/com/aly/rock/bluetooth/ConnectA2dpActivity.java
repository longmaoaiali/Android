package com.aly.rock.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.aly.rock.bluetooth.adapter.DeviceAdapter;
import com.aly.rock.bluetooth.utils.ReflectUtil;

/**
 * 作者：leavesC
 * 时间：2019/3/23 11:43
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
public class ConnectA2dpActivity extends BaseActivity {

    private DeviceAdapter deviceAdapter;

    private BluetoothAdapter bluetoothAdapter;

    private Handler handler = new Handler();

    private BluetoothA2dp bluetoothA2dp;

    private static final String TAG = "ConnectA2dpActivity";

    private BroadcastReceiver a2dpReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                        int connectState = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                        if (connectState == BluetoothA2dp.STATE_DISCONNECTED) {
                            showToast("已断开连接");
                        } else if (connectState == BluetoothA2dp.STATE_CONNECTED) {
                            DeviceAdapter.ViewHolder item = deviceAdapter.getCurrentviewHolder();
                            String str= item.tv_deviceStatus.getText().toString();
                            LogPrint(str);
                            str = str+"\n已连接";
                            LogPrint(str);
                            item.tv_deviceStatus.setText(str);
                            item.tv_deviceStatus.invalidate();
                            deviceAdapter.notifyDataSetChanged();
                            showToast("已连接");
                            hideLoadingDialog();

                        }
                        break;
                    case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                        int playState = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
                        if (playState == BluetoothA2dp.STATE_PLAYING) {
                            showToast("处于播放状态");
                        } else if (playState == BluetoothA2dp.STATE_NOT_PLAYING) {
                            showToast("未在播放");
                        }
                        break;
                }
            }
        }
    };

    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        showLoadingDialog("正在搜索蓝牙设备，搜索时间大约一分钟");
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        showToast("搜索蓝牙设备结束");
                        hideLoadingDialog();
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        deviceAdapter.addDevice(bluetoothDevice);
                        deviceAdapter.notifyDataSetChanged();
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                        BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (status == BluetoothDevice.BOND_BONDED) {
                            showToast(device.getName()+"已配对状态");
                            setPriority(device, 0);
                        } else if (status == BluetoothDevice.BOND_NONE) {
                            showToast(device.getName()+"未配对状态");
                        }
                        hideLoadingDialog();
                        break;
                }
            }
        }
    };

    private BroadcastReceiver otherBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        LogPrint("otherBluetoothReceiver action = "+action);
                        break;
                }
            }
        }
    };

    private BluetoothProfile.ServiceListener profileServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                LogPrint("A2dp onServiceDisconnected");
                bluetoothA2dp = null;
            }
        }

        @Override
        public void onServiceConnected(int profile, final BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                LogPrint("A2dp onServiceConnected");
                bluetoothA2dp = (BluetoothA2dp) proxy;
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceAdapter.getDevice(position);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                showToast("该设备已配对");
                showToast("开始连接");
                showLoadingDialog("开始连接");
                connectA2dp(device);
                return;
            }
            if (device.getBondState() != BluetoothDevice.BOND_NONE) {
                try {
                    showToast(device.getName()+"取消配对");
                    ReflectUtil.invokeMethod(device,"removeBond");
                    //Method tmpMethod = BluetoothDevice.class.getMethod("removeBond");
                    //tmpMethod.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {//非配对状态
                showToast("正在配对设备");
                device.createBond();//绑定设备
                return;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_a2dp);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("当前设备不支持蓝牙");
            finish();
            return;
        }
        bluetoothAdapter.getProfileProxy(this, profileServiceListener, BluetoothProfile.A2DP);
        initView();
        registerDiscoveryReceiver();
        registerOtherBluetoothReceiver();
        registerA2dpReceiver();
        startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,bluetoothA2dp);
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(a2dpReceiver);
        unregisterReceiver(discoveryReceiver);
        unregisterReceiver(otherBluetoothReceiver);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void initView() {
        ListView lv_deviceList = findViewById(R.id.lv_deviceList);
        deviceAdapter = new DeviceAdapter(this);
        lv_deviceList.setAdapter(deviceAdapter);
        lv_deviceList.setOnItemClickListener(itemClickListener);
    }

    private void registerDiscoveryReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//扫描开始
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描完成
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//扫描模式变化
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);//请求蓝牙打开的广播
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);///每发现1个蓝牙设备,都会有1条这类广播
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//蓝牙设备绑定状态的变化
        registerReceiver(discoveryReceiver, intentFilter);//注册
    }

    private void registerA2dpReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);//a2dp连接状态变化会发出此广播
        intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);//a2dp播放状态变化会发出此广播，有正在播放和未播放状态
        registerReceiver(a2dpReceiver, intentFilter);
    }
    private void registerOtherBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//ACL连接后会发出此广播
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//ACL断开后会发出此广播
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);//本地蓝牙适配器的连接状态的发生改变
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//本地蓝牙适配器的状态已更改,比如：蓝牙开关打开或关闭
        registerReceiver(otherBluetoothReceiver, intentFilter);
    }
    private void startScan() {
        if (!bluetoothAdapter.isEnabled()) {
            if (bluetoothAdapter.enable()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanDevice();
                    }
                }, 1500);
            } else {
                showToast("请求蓝牙权限被拒绝");
            }
        } else {
            scanDevice();
        }
    }

    private void scanDevice() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public void setPriority(BluetoothDevice device, int priority) {
        try {
            showToast("set priority = "+priority);
            //Method tmpMethod = BluetoothA2dp.class.getMethod("setPriority", BluetoothDevice.class,int.class);
            //tmpMethod.setAccessible(true);
            //tmpMethod.invoke(bluetoothA2dp, device, priority);
            ReflectUtil.invokeMethod(bluetoothA2dp,"setPriority",device,priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectA2dp(BluetoothDevice bluetoothDevice) {
        Method tmpMethod = null;
        try {
            tmpMethod = BluetoothA2dp.class.getMethod("disconnect", BluetoothDevice.class);
            tmpMethod.invoke(bluetoothA2dp, bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectA2dp(BluetoothDevice bluetoothDevice) {
        if (bluetoothA2dp == null || bluetoothDevice == null) {
            return;
        }
        int deviceType = bluetoothDevice.getType();
        if(deviceType == BluetoothDevice.DEVICE_TYPE_CLASSIC){//确认设备支持经典蓝牙（BR/EDR）
            bluetoothAdapter.cancelDiscovery();//连接前先取消扫描，扫描会占用带宽，影响连接效率和成功率
            try {
                Method connectMethod = BluetoothA2dp.class.getMethod("connect", BluetoothDevice.class);
                connectMethod.invoke(bluetoothA2dp, bluetoothDevice);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}