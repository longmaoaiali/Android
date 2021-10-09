package com.cvte.wifip2pfiletransfer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvte.wifip2pfiletransfer.Base.BaseActivity;
import com.cvte.wifip2pfiletransfer.adapter.DeviceAdapter;
import com.cvte.wifip2pfiletransfer.broadcast.DirectBroadcastReceiver;
import com.cvte.wifip2pfiletransfer.callback.DirectActionListener;
import com.cvte.wifip2pfiletransfer.common.Glide4Engine;
import com.cvte.wifip2pfiletransfer.model.FileTransfer;
import com.cvte.wifip2pfiletransfer.service.WifiClientTask;
import com.cvte.wifip2pfiletransfer.view.LoadingDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by user on 2020/11/2.
 */

public class SendFileActivity extends BaseActivity {
    private static final String TAG = "SendFileActivity";
    private Button mBtn_disconnect;
    private Button mBtn_chooseFile;
    private TextView mTv_deviceInfo;
    private TextView mTv_deviceName;
    private TextView mTv_deviceAddress;
    private TextView mTv_deviceStatus;
    private TextView mTv_status;
    private TextView mTv_deviceList;
    private RecyclerView mRv_deviceList;

    private LoadingDialog mLoadingDialog;

    private WifiP2pManager mWifiP2pManager;
    /**
     * A channel that connects the application to the Wifi p2p framework.
     * Most p2p operations require a Channel as an argument. An instance of Channel is obtained
     */
    private WifiP2pManager.Channel mChannel;
    private WifiP2pInfo mWifiP2pInfo;
    private boolean mWifiP2pEnable = false;
    private static final int CODE_CHOOSE_FILE = 100;


    private DirectActionListener mDirectActionListener = new DirectActionListener() {

        @Override
        public void setWifiP2pEnabled(boolean enabled) {
            mWifiP2pEnable = enabled;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            dismissLoadingDialog();
            mWifiP2pDeviceList.clear();
            mDeviceAdapter.setData(mWifiP2pDeviceList);
            mBtn_chooseFile.setEnabled(true);
            mBtn_disconnect.setEnabled(true);
            //是否成功创建群组
            Log.d(TAG,"onConnectionInfoAvailable groupFormed-->" + wifiP2pInfo.groupFormed);
            Log.d(TAG,"onConnectionInfoAvailable isGroupOwner-->" + wifiP2pInfo.isGroupOwner);
            Log.d(TAG,"onConnectionInfoAvailable groupOwnerAddress-->" + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("连接的设备名：");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("连接的设备的地址：");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }
            stringBuilder.append("\n");
            stringBuilder.append("是否群主：");
            stringBuilder.append(wifiP2pInfo.isGroupOwner ? "是群主" : "非群主");
            stringBuilder.append("\n");
            stringBuilder.append("群主IP地址：");
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
            //todo:
            mTv_status.setText(stringBuilder);
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                SendFileActivity.this.mWifiP2pInfo = wifiP2pInfo;
            }

        }

        @Override
        public void onDisconnection() {
            Log.e(TAG,"onDisconnection callback");
            mBtn_disconnect.setEnabled(false);
            mBtn_chooseFile.setEnabled(false);

            showToast("处于非连接中");
            mWifiP2pDeviceList.clear();
            mDeviceAdapter.setData(mWifiP2pDeviceList);
            mTv_status.setText(null);
            SendFileActivity.this.mWifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable callback");
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName);
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Log.e(TAG, "Status: " + wifiP2pDevice.status);

            mTv_deviceName.setText(wifiP2pDevice.deviceName);
            mTv_deviceAddress.setText(wifiP2pDevice.deviceAddress);
            mTv_deviceStatus.setText(MainActivity.getDeviceStatus(wifiP2pDevice.status));
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Log.d(TAG,"inner class DirectActionListener method onPeersAvailable ");
            SendFileActivity.this.mWifiP2pDeviceList.clear();
            SendFileActivity.this.mWifiP2pDeviceList.addAll(wifiP2pDeviceList);
            mDeviceAdapter.setData(SendFileActivity.this.mWifiP2pDeviceList);
            mLoadingDialog.cancel();
        }

        @Override
        public void onChannelDisconnected() {
            Log.d(TAG,"onChannelDisconnected-->");
        }
    };
    private List<WifiP2pDevice> mWifiP2pDeviceList = new ArrayList<>();
    private DeviceAdapter mDeviceAdapter;
    private WifiP2pDevice mWifiP2pDevice;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfile);

        initP2PEvent();
        initView();
        initViewListner();

    }

    public void initP2PEvent() {
        mWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (mWifiP2pManager == null) {
            finish();
            return;
        }

        mChannel = mWifiP2pManager.initialize(this,getMainLooper(),mDirectActionListener);
        DirectBroadcastReceiver directBroadcastReceiver = new DirectBroadcastReceiver(mWifiP2pManager,mChannel,mDirectActionListener);
        registerReceiver(directBroadcastReceiver,directBroadcastReceiver.getIntentFilter());

        //startP2PDiscovery();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startP2PDiscovery();

    }


    private void initViewListner() {

        mDeviceAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WifiP2pDevice wifiP2pDevice) {
                mWifiP2pDevice = wifiP2pDevice;
                showToast(wifiP2pDevice.deviceName);
                connect();
            }
        });

        mBtn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        mBtn_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navToChose();
            }
        });
    }

    /*根据mWifiP2pDevice 发起连接*/
    private void connect() {
        showToast("连接 "+mWifiP2pDevice.deviceName+" ing ");
        //todo:发起连接
        WifiP2pConfig p2pConfig = new WifiP2pConfig();
        if (p2pConfig.deviceAddress != null && mWifiP2pDevice != null) {
            p2pConfig.deviceAddress = mWifiP2pDevice.deviceAddress;
            //PBC WIFI protected setup with PBC push-button configure method
            //WIFI 直连中 group owner实现数据加密
            p2pConfig.wps.setup = WpsInfo.PBC;
            showLoadingDialog("正在连接"+mWifiP2pDevice.deviceName);
            mWifiP2pManager.connect(mChannel, p2pConfig, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG,"connect success");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败"+reason);
                    dismissLoadingDialog();
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CHOOSE_FILE && resultCode == RESULT_OK) {
            List<String> strings = Matisse.obtainPathResult(data);
            if (strings != null && !strings.isEmpty()) {
                String path = strings.get(0);
                Log.e(TAG,"文件路径:"+path);
                File file = new File(path);
                if (file.exists() && mWifiP2pInfo!= null) {
                    FileTransfer fileTransfer = new FileTransfer(file.getPath(),file.length());
                    //TODO:开始文件传输
                    /*execute 传入groupowner 的IP地址*/
                    new WifiClientTask(this,fileTransfer).execute(mWifiP2pInfo.groupOwnerAddress.getHostAddress());
                }
            }
        }
    }

    private void navToChose() {
        Log.d(TAG,"choose File");
        /*Matisse 图片 视频文件选择框架*/
        /*Matisse is a well-designed local image and video selector for Android*/
        /*github : https://github.com/zhihu/Matisse*/
        /* 选择结果会回调 onActivityResult()*/
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .capture(false)
                .captureStrategy(new CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".fileprovider"))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.70f)
                .imageEngine(new Glide4Engine())
                .forResult(CODE_CHOOSE_FILE);

    }

    private void disconnect() {
    }

    private void initView() {
        setTitle("发送文件");
        mBtn_disconnect = this.findViewById(R.id.btn_disconnect);
        mBtn_chooseFile = this.findViewById(R.id.btn_chooseFile);
        mBtn_disconnect.setEnabled(false);
        mBtn_chooseFile.setEnabled(false);
        mTv_deviceInfo = this.findViewById(R.id.tv_deviceInfo);
        mTv_deviceName = this.findViewById(R.id.tv_deviceName);
        mTv_deviceAddress = this.findViewById(R.id.tv_deviceAddress);
        mTv_deviceStatus = this.findViewById(R.id.tv_deviceStatus);
        mTv_status = this.findViewById(R.id.tv_status);
        mTv_deviceList = this.findViewById(R.id.tv_deviceList);
        mRv_deviceList = this.findViewById(R.id.rv_deviceList);


        mLoadingDialog = new LoadingDialog(this);

        mDeviceAdapter = new DeviceAdapter();
        mRv_deviceList.setLayoutManager(new LinearLayoutManager(this));
        mRv_deviceList.setAdapter(mDeviceAdapter);
    }

    /*创建好顶部的菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action,menu);
        return true;
    }

    /*顶部菜单添加事件*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDirectEnable:{
                if (mWifiP2pManager != null && mChannel != null) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } else {
                    showToast("当前设备不支持WFD");
                }
                return true;
            }

            case R.id.menuDirectDiscover:{
                startP2PDiscovery();
                return true;
            }
            default:
                return true;

        }
    }

    private void startP2PDiscovery() {
        if (!mWifiP2pEnable) {
            showToast("请打开wifi开关");
            return;
        }
        mLoadingDialog.show("正在搜索附近的设备",true,false);
        mWifiP2pDeviceList.clear();
        //先清空列表
        mDeviceAdapter.setData(mWifiP2pDeviceList);
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                /*搜索成功会有广播,在广播接收者回调显示recyclerListView*/
                showToast("搜索成功");
            }

            @Override
            public void onFailure(int reason) {
                showToast("搜索失败");
                Log.d(TAG,"reason code is "+reason);
                mLoadingDialog.cancel();
            }
        });
    }


}
