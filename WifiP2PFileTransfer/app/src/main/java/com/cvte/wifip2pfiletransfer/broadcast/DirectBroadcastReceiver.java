package com.cvte.wifip2pfiletransfer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.cvte.wifip2pfiletransfer.callback.DirectActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/11/3.
 */

public class DirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DirectBroadcastReceiver";

    private WifiP2pManager mWifiP2pManager;

    private WifiP2pManager.Channel mChannel;

    private DirectActionListener mDirectActionListener;

    public DirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, DirectActionListener directActionListener){
        mWifiP2pManager = wifiP2pManager;
        mChannel = channel;
        mDirectActionListener = directActionListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                //处理WFD enable disable
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:{
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-100);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                        mDirectActionListener.setWifiP2pEnabled(false);
                        /*清空recyclerView列表*/
                        List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
                        mDirectActionListener.onPeersAvailable(wifiP2pDeviceList);

                    } else {
                        mDirectActionListener.setWifiP2pEnabled(true);
                    }
                    break;
                }
                //处理P2P设备列表变化的广播
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:{
                    mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            mDirectActionListener.onPeersAvailable(peers.getDeviceList());
                        }
                    });
                    break;
                }

                //对端P2P设备 发生了变化
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:{
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null && networkInfo.isConnected()) {
                        mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                            @Override
                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                mDirectActionListener.onConnectionInfoAvailable(info);
                            }
                        });
                        Log.d(TAG,"已连接P2P设备");
                    } else {
                        mDirectActionListener.onDisconnection();
                        Log.d(TAG,"P2P设备已断开");
                    }
                    break;
                }

                //本P2P设备发生了变化
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:{
                    WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    mDirectActionListener.onSelfDeviceAvailable(wifiP2pDevice);
                    break;
                }

                default:
                    Log.d(TAG,"other intent-->"+intent.toString());
                    break;


            }
        }
    }


    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return  intentFilter;
    }
}
