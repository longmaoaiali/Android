package com.cvte.wifip2pfiletransfer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.wifip2pfiletransfer.Base.BaseActivity;
import com.cvte.wifip2pfiletransfer.broadcast.DirectBroadcastReceiver;
import com.cvte.wifip2pfiletransfer.callback.DirectActionListener;
import com.cvte.wifip2pfiletransfer.model.FileTransfer;
import com.cvte.wifip2pfiletransfer.service.WifiServerService;

import java.io.File;
import java.util.Collection;

/**
 * Created by user on 2020/11/2.
 */

public class ReceiveFileActivity extends BaseActivity {

    private static final String TAG = "ReceiveFileActivity";
    private ImageView mCover;
    private Button mCreateGroup;
    private Button mRemoveGroup;
    private TextView mTvLog;
    private ProgressDialog mProgressDialog;
    private WifiP2pManager mWifiP2pManager;
    private WifiServerService mWifiServerService;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //todo:成功绑定文件传输的服务
            Log.d(TAG,"onServiceConnected mWifiServerService = binder.getService();");
            WifiServerService.MyBinder binder = (WifiServerService.MyBinder) service;
            mWifiServerService = binder.getService();
            mWifiServerService.setOnProgressChangListener(new WifiServerService.OnProgressChangListener() {
                @Override
                public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setMessage("文件名："+new File(fileTransfer.getFilePath()).getName());
                            mProgressDialog.setProgress(progress);
                            mProgressDialog.show();
                        }
                    });
                }

                @Override
                public void onTransferFinished(final File file) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.cancel();
                            if (file != null && file.exists()) {
                                Glide.with(ReceiveFileActivity.this).load(file.getPath()).into(mCover);
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mWifiServerService = null;
            bindService();
        }
    };
    private boolean mConnectionInfoAvailable = false;

    private void log(String log) {
        mTvLog.append(log + "\n");
        mTvLog.append("----------" + "\n");
    }

    private DirectActionListener mDirectActionListener = new DirectActionListener() {
        @Override
        public void setWifiP2pEnabled(boolean enabled) {
            log("wifiP2pEnabled: " + enabled);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            log("onConnectionInfoAvailable");
            log("isGroupOwner：" + wifiP2pInfo.isGroupOwner);
            log("groupFormed：" + wifiP2pInfo.groupFormed);

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                mConnectionInfoAvailable = true;

                if (mWifiServerService != null) {
                    Log.d(TAG,"startService");
                    startService(WifiServerService.class);
                } else {
                    Log.d(TAG,"startService no action ,mWifiServerService is null");
                }
            }
        }

        @Override
        public void onDisconnection() {
            mConnectionInfoAvailable = false;
            log("onDisconnection");
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            log("onSelfDeviceAvailable");
            log(wifiP2pDevice.toString());
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            log("onPeersAvailable,size:" + wifiP2pDeviceList.size());
            for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
                log(wifiP2pDevice.toString());
            }
        }

        @Override
        public void onChannelDisconnected() {
            log("onChannelDisconnected");
        }
    };
    private WifiP2pManager.Channel mChannel;
    private DirectBroadcastReceiver mDirectBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivefile);
        initView();
        initViewListener();

        mWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (mWifiP2pManager == null) {
            finish();
            return;
        }

        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), mDirectActionListener);

        mDirectBroadcastReceiver = new DirectBroadcastReceiver(mWifiP2pManager,mChannel,mDirectActionListener);
        registerReceiver(mDirectBroadcastReceiver,DirectBroadcastReceiver.getIntentFilter());
        bindService();
    }

    private void initViewListener() {
        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        log("createGroup onSuccess");
                        dismissLoadingDialog();
                        showToast("onSuccess");
                    }

                    @Override
                    public void onFailure(int reason) {
                        log("createGroup onFailure: " + reason);
                        dismissLoadingDialog();
                        showToast("onFailure");
                    }
                });
            }
        });

        mRemoveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroup();
            }
        });
    }

    private void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("removeGroup onSuccess");
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                log("removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }

    private void bindService() {
        Log.d(TAG,"bindService bind fileTransfer service");
        Intent intent = new Intent(ReceiveFileActivity.this,WifiServerService.class);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }


    private void initView() {
        setTitle("接收文件");
        mCover = this.findViewById(R.id.iv_image);
        mCreateGroup = this.findViewById(R.id.btn_createGroup);
        mRemoveGroup = this.findViewById(R.id.btn_removeGroup);
        mTvLog = this.findViewById(R.id.tv_log);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在接收文件");
        mProgressDialog.setMax(100);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWifiServerService != null) {
            mWifiServerService.setOnProgressChangListener(null);
            unbindService(mServiceConnection);
        }
        unregisterReceiver(mDirectBroadcastReceiver);
        stopService(new Intent(this, WifiServerService.class));
        if (mConnectionInfoAvailable) {
            removeGroup();
        }
    }
}
