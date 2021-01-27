package com.ly.miracast;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pWfdInfo;
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

import java.lang.reflect.Method;
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
            Manifest.permission.WAKE_LOCK

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
    private TextView mTextViewPeerDeviceList;
    private WifiP2pDevice mSelectDevice;
    private boolean mWfdEnabled=false;



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

        Log.d(TAG,"onCreate");
        initView();
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

    private void initView() {
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        mTextViewPeerDeviceList = (TextView) findViewById(R.id.peer_devices);

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
                Log.d(TAG,"mSpinner View select "+position);
                switch(position){
                    case 1: //start scan
                        tryDiscoverPeers();
                        break;
                    case 2://stop scan
                        stopPeerDiscovery();
                        break;
                    case 3://start listen
                        startListen();
                        break;
                    case 4:
                        stopListen();
                        break;
                    case 5:
                        creatGroup();
                    case 6:
                        removeGroup();
                    case 7://connect to peer by pin
                        if (mSelectDevice==null) {
                            Toast.makeText(MainActivity.this,"select device is null",Toast.LENGTH_SHORT);
                        } else {
                            startConnect(mSelectDevice,1);
                        }
                    case 8://connect to peer by push button
                        if (mSelectDevice==null) {
                            Toast.makeText(MainActivity.this,"select device is null",Toast.LENGTH_SHORT);
                        } else {
                            startConnect(mSelectDevice,0);
                        }
                        break;
                    case 9:// Set wfd session manual mode
                        if (mSelectDevice==null) {
                            Toast.makeText(MainActivity.this,"select device is null",Toast.LENGTH_SHORT);
                        } else {
                            startConnect(mSelectDevice,2);
                        }
                        break;
                    case 10:// Set wfd session auto mode
                        setWfdSessionMode(true);
                        break;
                    case 11:
                        setWfdSessionMode(false);
                        break;
                    case 12:// Continue remaining wfd session under manual mode
                        startWfdSession();
                        break;
                    case 13://set role sink
                        changeRole(true);
                        break;
                    case 14:
                        changeRole(false);
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

    @TargetApi(30)
    private void reflectSetWFDInfoMethod(WifiP2pManager.Channel channel, WifiP2pWfdInfo wifiP2pWfdInfo, WifiP2pManager.ActionListener listener){
        try {
            Class<?> clazz = Class.forName("android.net.wifi.p2p.WifiP2pManager");
            Class[] functionArgs = new Class[3];
            functionArgs[0] = WifiP2pManager.Channel.class;
            functionArgs[1] = WifiP2pWfdInfo.class;
            functionArgs[2] = WifiP2pManager.ActionListener.class;
            Method connect = mWifiP2pManager.getClass().getDeclaredMethod("setWFDInfo", functionArgs);
            //connect.setAccessible(true);
            connect.invoke(mWifiP2pManager,channel,wifiP2pWfdInfo,listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //APILevel >= 30
    @TargetApi(30)
    private void changeRole(boolean isSink) {
        if(!isNetAvailiable()){
            return;
        }
        //todo:WifiP2pWfdInfo
        WifiP2pWfdInfo wfdInfo = new WifiP2pWfdInfo();
        if(isSink){
            mWfdEnabled = true;
            wfdInfo.setDeviceType(WifiP2pWfdInfo.DEVICE_TYPE_PRIMARY_SINK);
            wfdInfo.setSessionAvailable(true);
            wfdInfo.setControlPort(7236);
            wfdInfo.setMaxThroughput(50);
        } else {
            mWfdEnabled = false;
        }

        reflectSetWFDInfoMethod(mChannel, wfdInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully set WFD info.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to set WFD info with reason " + reason + ".");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.changeRole(true);
            }
        });

        //mChannel
        /*
        mWifiP2pManager.setWFDInfo(mChannel, wfdInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully set WFD info.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to set WFD info with reason " + reason + ".");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MainActivity.this.changeRole(true);
            }
        });
        */
    }

    private boolean isNetAvailiable() {
        if(mWifiManager != null){
            int state = mWifiManager.getWifiState();
            if(WifiManager.WIFI_STATE_ENABLING == state
                    || WifiManager.WIFI_STATE_ENABLED == state){
                Log.d(TAG,"WIFI enabled");
                return true;
            } else {
                Log.d(TAG,"WIFI disabled");
                return false;
            }
        }
        return false;
    }

    private String mPort;
    private String mIP;
    public static final String HRESOLUTION_DISPLAY = "display_resolution_hd";
    private void startWfdSession() {
        //手动建立会话
        if(mManualInitWfdSession){
            Log.d(TAG,"start Manual WfdSession");
            setConnect();
            Log.d(TAG,"start miracast");
            Intent intent = new Intent(MainActivity.this,SinkActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(SinkActivity.KEY_PORT, mPort);
            bundle.putString(SinkActivity.KEY_IP, mIP);
            bundle.putBoolean(HRESOLUTION_DISPLAY,mSharedPreferences.getBoolean(HRESOLUTION_DISPLAY,true));
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
          Log.d(TAG,"error Auto WfdSessionMode,return");
          return;
        }
    }

    //UI show
    private void setConnect() {
        Log.d(TAG,"show wifi yes");
        /*
        mConnectDesc.setText (getString (R.string.connected_info) );
        mConnectStatus.setBackgroundResource (R.drawable.wifi_yes);
        */
    }

    public boolean mManualInitWfdSession = false;
    private void setWfdSessionMode(boolean mode) {
        Log.d(TAG, "setWfdSessionMode " + mode);
        mManualInitWfdSession = mode;
    }

    //config WCS(WPS) 是 wifi simple configuration 的 method
    private void startConnect(WifiP2pDevice selectDevice, int config) {
        if (config > WpsInfo.KEYPAD) {
            Log.d(TAG,"startConnect config invalid.");
        }

        if(config == WpsInfo.PBC)
            Log.d(TAG,"startConnect PBC configuration");
        else if(config == WpsInfo.DISPLAY)
            Log.d(TAG,"startConnect DISPLAY configuration");
        else if(config == WpsInfo.KEYPAD)
            Log.d(TAG,"startConnect KEYPAD configuration");

        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        WpsInfo wpsInfo = new WpsInfo();

        wpsInfo.setup = config;
        wifiP2pConfig.wps = wpsInfo;
        wifiP2pConfig.deviceAddress = selectDevice.deviceAddress;
        //max intent for GO
        wifiP2pConfig.groupOwnerIntent = 15;
        mStartConnecting = true;

        mWifiP2pManager.connect(mChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"startConnect success");
            }

            @Override
            public void onFailure(int reason) {
                mStartConnecting =false;
                Log.d(TAG,"startConnect failed with reason "+reason);
            }
        });

    }

    private void removeGroup() {
        mForceStopScan = false;
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "removeGroup Success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "removeGroup Failure");
            }
        });
    }

    private void creatGroup() {
        mForceStopScan = true;
        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"creatGroup success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"creatGroup failed");
            }
        });
    }

    private void startListen() {
        Log.d(TAG, "WifiP2pManager.listen call failed");
        /*
        mWifiP2pManager.listen(mChannel,true, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "start listen peers succeed.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "start listen peers failed with reason " + reason + ".");
            }
        });
        */
    }

    private void stopListen() {
        Log.d(TAG, "WifiP2pManager.listen call failed");
        /*
        mWifiP2pManager.listen(mChannel,false, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "start listen peers succeed.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "start listen peers failed with reason " + reason + ".");
            }
        });
        */
    }

    private void stopPeerDiscovery() {
        Log.d(TAG,"stopDiscover, do ForceStopScan");
        mForceStopScan = true;
        Log.d(TAG,"WPM.stopDiscovery",new Throwable());
        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"Stop peer discovery succeed.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"Stop peer discovery failed with reason "+reason);
            }
        });
    }

    private boolean mForceStopScan = false;
    /*start scan if success then to requestPeers*/
    private void tryDiscoverPeers() {
        mForceStopScan = false;
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"Discovery peers succeed, Requesting peers now");
                //request will be null , advertise to listen broadcast
                requestPeers();
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"Discovery peers failed with reason "+ reason);
            }
        });

    }


    /*Request the current list of peers： just get list not to discovery */
    private ArrayList<WifiP2pDevice> mWifiP2pDeviceList = new ArrayList<WifiP2pDevice>();
    //private TextView mPeersDeviceList;
    private void requestPeers() {
        mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.d(TAG,"Received list of peers");

                mWifiP2pDeviceList.clear();
                if(!peers.getDeviceList().isEmpty()){
                    Log.d(TAG,"device list is not null");
                    for (WifiP2pDevice device : peers.getDeviceList()){
                        Log.d(TAG,"liuyu "+describeWifiP2pDevice(device));
                        mWifiP2pDeviceList.add(device);
                    }

                    String list = MainActivity.this.getResources().getString(R.string.peer_list);
                    for(int i=0; i < mWifiP2pDeviceList.size();i++){
                        list += " "+mWifiP2pDeviceList.get(i).deviceName;
                        Log.d(TAG,"onPeersAvailable peerDevice: " + mWifiP2pDeviceList.get(i).deviceName+", status:"+mWifiP2pDeviceList.get(i).status + " (0-CONNECTED,3-AVAILABLE)");
                    }
                    //show on TextView
                    mTextViewPeerDeviceList.setText(list);
                } else {
                    Log.d(TAG,"device list is null");
                    mTextViewPeerDeviceList.setText("device list is null");
                }

            }
        });
    }

    private String describeWifiP2pDevice(WifiP2pDevice device) {
        return device != null ? device.toString().replace('\n',','):"null";
    }

    private ArrayList<String> mFunList;
    private void initFunList() {
        mFunList = new ArrayList<String>();
        mFunList.add(" Function List : ");
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
