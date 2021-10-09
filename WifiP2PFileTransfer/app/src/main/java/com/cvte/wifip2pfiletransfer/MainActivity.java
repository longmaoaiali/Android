package com.cvte.wifip2pfiletransfer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cvte.wifip2pfiletransfer.Base.BaseActivity;
import com.cvte.wifip2pfiletransfer.broadcast.DirectBroadcastReceiver;


public class MainActivity extends BaseActivity {

    private Button mCheckPermissionBT;
    private Button mSendFileBT;
    private Button mReceiveFileBT;
    private static final int mRequestCode = 664;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initViewListener();

    }



    private void initViewListener() {
        mCheckPermissionBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        mSendFileBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"send File-->");
                startActivity(SendFileActivity.class);
            }
        });

        mReceiveFileBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"receive File-->");
                startActivity(ReceiveFileActivity.class);
            }
        });
    }

    private void checkPermission() {
        String[] permissions =  new String[]{
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
        };
        requestPermissions(permissions,mRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    showToast("缺少权限");
                    return;
                }
            }
            showToast("已获得权限");
        }
    }

    private void initView() {
        mCheckPermissionBT = this.findViewById(R.id.checkPermission);
        mSendFileBT = this.findViewById(R.id.sendFile);
        mReceiveFileBT = this.findViewById(R.id.receiveFile);
    }


    public static String getDeviceStatus(int deviceStatus){
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "可用的";
            case WifiP2pDevice.INVITED:
                return "邀请中";
            case WifiP2pDevice.CONNECTED:
                return "已连接";
            case WifiP2pDevice.FAILED:
                return "失败的";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用的";
            default:
                return "未知的";
        }
    }

}
