package com.person.wifitest;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mScanResultList;
    private List<WifiConfiguration> mWifiConfigurationList;

    public WifiAdmin(Context context){
        //获取wifimanager
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public WifiManager getWifiManager() { return mWifiManager; }

    public void openWifi() {
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    public String getIPAddress(){
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo==null)?"0":formatIPAddress(mWifiInfo.getIpAddress());
    }

    public static String formatIPAddress(int ipAddress){
        return (ipAddress & 0xFF)+"."+
                ((ipAddress>>8) & 0xFF)+"."+
                ((ipAddress>>16) & 0xFF)+"."+
                ((ipAddress>>24) & 0xFF);
    }
}
