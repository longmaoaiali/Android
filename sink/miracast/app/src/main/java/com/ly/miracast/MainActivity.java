package com.ly.miracast;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    public static final String WIFI_P2P_IP_ADDR_CHANGED_ACTION = "android.net.wifi.p2p.IPADDR_INFORMATION";

    private static final String TAG = "MainActivity";

    //todo:maybe more permission
    private static String[] smPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
    };
    private static int smRequestPermissionCode = 644;

    private final IntentFilter mIntentFilter = new IntentFilter();
    private WifiP2pManager mWifiP2pManager;
    private WifiManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mBroadcastReceiver;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private PowerManager.WakeLock mWakeLock;
    private boolean mFirstInit = false;

    public boolean mStartConnecting = false;

    private void requestPermissions(){
        requestPermissions(smPermissions,smRequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == smRequestPermissionCode){
            for (int grantResult : grantResults) {
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"获取权限失败",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "获取权限成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //申请权限
        requestPermissions();
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Log.d(TAG,"onCreate");
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        //自定义的广播
        mIntentFilter.addAction(WIFI_P2P_IP_ADDR_CHANGED_ACTION);
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        /*
        * A channel that connects the application to the Wifi p2p framework.
        * Most p2p operations require a Channel as an argument.
        */
        mChannel = mWifiP2pManager.initialize(this,getMainLooper(),null);
        mBroadcastReceiver = new WiFiDirectBroadcastReceiver(mWifiP2pManager,mChannel,this);
        //创建SharedPreferences 保存在/data/data/包名/shared_prefs目录下
        mSharedPreferences = this.getSharedPreferences("settings_info", MODE_PRIVATE);
        //进入编辑模式 后面需要
        mSharedPreferencesEditor = mSharedPreferences.edit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo:onResume
        Log.d(TAG,"onResume");
        /*Remove the current p2p group*/
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"removeGroup Success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"removeGroup failed");
            }
        });

        //enable backlight
        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,TAG);
        mWakeLock.acquire();
        if(mFirstInit){
            //第二次运行时需要remove 滞留广播
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            removeStickyBroadcast(intent);
        } else {
            mFirstInit = true;
        }
        //miracast certification todo:
        initCert();


    }

    private ArrayList<String> mDeviceList;
    private Spinner mSpinner, mSpinDevice;
    private InputMethodManager mInputMethodManager;
    private void initCert() {
        //todo:
        initFunList();
        mStartConnecting = false;
        mDeviceList = new ArrayList<String>();
        mDeviceList.add(" ");

        mSpinner = (Spinner)findViewById(R.id.spinner1);
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mFunList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setPrompt("Please function！");

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 1: //start scan
                        Log.d(TAG,"mSpinner View select"+position);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private ArrayList<String> mFunList;
    private void initFunList() {
        mFunList = new ArrayList<String>();
        mFunList.add(" ");
        mFunList.add(" ");
        mFunList.add("Start scan");
        mFunList.add("Stop scan");
        mFunList.add("Start listen");
        mFunList.add("Stop listen");
        mFunList.add("Enable autonomous GO");
        mFunList.add("Disable autonomous GO");
        mFunList.add("Connect to peer by display_pin");
        mFunList.add("Connect to peer by push_button");
        mFunList.add("Connect to peer by enter_pin");
        mFunList.add("Set wfd session manual mode");
        mFunList.add("Set wfd session auto mode");
        mFunList.add("Continue remaining wfd session under manual mode");
        mFunList.add("Enable wfd");
        mFunList.add("Disable wfd");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
        //todo
        mWakeLock.release();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
