package com.person.wifitest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WifiTest";
    private static final String testHost = "www.baidu.com";
    private StringBuffer stringBuffer;
    private WifiAdmin wifiAdmin;
    private Button btnOpen;
    private Button btnClose;
    private TextView textViewMess;
    private TextView textViewTestCount;
    private TextView textViewSuccessCount;
    private TextView textViewFailCount;
    private int openTime = 0;
    private int stopTime = 0;
    private EditText editTextOpenTime;
    private EditText editTextStopTime;
    private EditText editTextTestNum;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private WifiConfiguration wifiInfo;
    private EditText editTextSSID;
    private EditText editTextPasswd;
    //private RadioButton ch1;
    //private RadioButton ch2;
    private TextView textViewIp;

    private boolean startFlag = false;

    private static final int MSG_WIFI_STARTSCAN = 1;
    private static final int MSG_WIFI_OPENWIFI = 2;
    private static final int MSG_WIFI_OPENWIFIFAIL = 3;
    private static final int MSG_WIFI_CLOSEWIFIFAIL = 4;
    private static final int MSG_WIFI_GETWIFILISTFAIL = 5;
    private static final int MSG_WIFI_PINGBAIDU = 6;
    private static final int MSG_WIFI_WAITMESSAGE = 7;
    private static final int MSG_WIFI_CLOSEWIFI = 8;
    private static final int DELAY_TIME = 10000;

    private int testCount = 0;
    private int failCount = 0;
    private int successCount = 0;
    private boolean isping = false;
    private boolean isFailed = false;
    private boolean isFinishScan = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WIFI_STARTSCAN:
                    LogManager.d(TAG, "handleMessage: startScan");
//                    initWifiList();
                    //WifiConfiguration wifiConfiguration = wifiAdmin.createWifiInfo("helloworld","xtee915920",3);
                    //wifiAdmin.addNetwork(wifiConfiguration);
                    isFinishScan = true;
                    String ipAddress = wifiAdmin.getIPAddress();
                    textViewIp.setText("IPAddress :"+ipAddress);
                    if(ipAddress.equals("0.0.0.0")){
                        sendWifiMessage(MSG_WIFI_CLOSEWIFIFAIL, 0);
                    }else {
                        sendWifiMessage(MSG_WIFI_CLOSEWIFI, 500);
                    }
                    break;
                case MSG_WIFI_CLOSEWIFI:
                    LogManager.d(TAG, "handleMessage: close wifi");
                    wifiAdmin.closeWifi();
                    if(wifiAdmin.checkState()!=WifiManager.WIFI_STATE_DISABLED){
                        sendWifiMessage(MSG_WIFI_CLOSEWIFIFAIL, DELAY_TIME);
                    }
                    break;
                case MSG_WIFI_OPENWIFI:
                    //textViewMess.setText("");
                    LogManager.d(TAG, "handleMessage: open wifi");
                    //if(wifiAdmin.checkState()==WifiManager.WIFI_STATE_DISABLED&&testCount==0){

                    //}//else {
                    testCount++;
                    //}
                    if(testCount > Integer.parseInt(editTextTestNum.getText().toString())){
                        testCount--;
                        startFlag = false;
                        handler.removeCallbacksAndMessages(null);
                        btnOpen.setEnabled(true);
                        btnClose.setEnabled(false);
                        editTextOpenTime.setEnabled(true);
                        editTextStopTime.setEnabled(true);
                        editTextTestNum.setEnabled(true);
                        //ch1.setEnabled(true);
                        //ch2.setEnabled(true);
                        textViewMess.setText("完成！共测试："+String.valueOf(testCount)+"次，成功："+String.valueOf(successCount)+"次，失败："+String.valueOf(failCount)+"次");
                        unregisterReceiver(networkConnectChangedReceiver);
                        testCount = 0;
                        failCount = 0;
                        successCount = 0;
                        break;
                    }
                    textViewTestCount.setText("进行第："+String.valueOf(testCount)+"次测试");
                    wifiAdmin.openWifi();
                    if(wifiAdmin.checkState() != WifiManager.WIFI_STATE_ENABLED){
                        sendWifiMessage(MSG_WIFI_OPENWIFIFAIL, DELAY_TIME);
                    }
                    break;
                case MSG_WIFI_OPENWIFIFAIL:
                    LogManager.d(TAG, "handleMessage: open wifi failed");
                    if(wifiAdmin.checkState()!=WifiManager.WIFI_STATE_ENABLED){
                        LogManager.d(TAG, "open fail wifi status: "+String.valueOf(wifiAdmin.checkState()));
                        failCount++;
                        textViewFailCount.setText("失败次数："+String.valueOf(failCount)+"次");
                        unregisterReceiver(networkConnectChangedReceiver);
                        registerWifiReciver();
                        sendWifiMessage(MSG_WIFI_CLOSEWIFI,0);
                        isFailed = true;
                    }
                    LogManager.d(TAG, "failCount: "+String.valueOf(failCount));
                    break;
                case MSG_WIFI_CLOSEWIFIFAIL:
                    LogManager.d(TAG, "handleMessage: close wifi failed");
                    if(wifiAdmin.checkState() != WifiManager.WIFI_STATE_DISABLED){
                        LogManager.d(TAG, "close fail wifi status: "+String.valueOf(wifiAdmin.checkState()));
                        failCount++;
                        textViewFailCount.setText("失败次数："+String.valueOf(failCount)+"次");
                        unregisterReceiver(networkConnectChangedReceiver);
                        registerWifiReciver();
                        sendWifiMessage(MSG_WIFI_OPENWIFI, 0);
                    }
                    LogManager.d(TAG, "failCount: "+String.valueOf(failCount));
                    break;
                case MSG_WIFI_GETWIFILISTFAIL:
                    LogManager.d(TAG, "handleMessage: get wifi stat");
                    failCount++;
                    textViewFailCount.setText("失败次数："+String.valueOf(failCount)+"次");
                    LogManager.d(TAG, "get wifi stat failCount: "+String.valueOf(failCount));
                    break;
                case MSG_WIFI_PINGBAIDU:
                    try{
                        wifiInfo = wifiAdmin.createWifiInfo("helloworld","xtee915920",3);
                        wifiAdmin.addNetwork(wifiInfo);
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    boolean status = false;
                    status = ping(testHost,2,stringBuffer);
                    LogManager.d(TAG, "status:"+String.valueOf(status));
                    if(stringBuffer!=null){
                        textViewMess.setText(stringBuffer.toString());
                    }
                    if(!status){
                        failCount++;
                        successCount--;
                        Log.d(TAG, "失败次数: "+String.valueOf(failCount));
                        textViewFailCount.setText("失败次数："+String.valueOf(failCount)+"次");
                    }
                    wifiAdmin.closeWifi();
                    if(wifiAdmin.checkState()!=WifiManager.WIFI_STATE_DISABLED){
                        sendWifiMessage(MSG_WIFI_CLOSEWIFIFAIL,DELAY_TIME);
                    }
                    break;
                case MSG_WIFI_WAITMESSAGE:
                    textViewIp.setText("等待获取IP地址");
                    textViewMess.setText("wifi 已开启，等待时间到达后进行测试！");
                    break;
                default:
                    break;
            }
        }
    };

    private void sendWifiMessage(int mes,int second){
        Message message = new Message();
        message.what=mes;
        handler.sendMessageDelayed(message,second);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiAdmin = new WifiAdmin(this);
        btnOpen = (Button) findViewById(R.id.btnStart);
        btnOpen.setOnClickListener(startOnClickListener);
        btnClose = (Button) findViewById(R.id.btnStop);
        btnClose.setOnClickListener(stopOnClickListener);
        textViewMess = (TextView) findViewById(R.id.messageShow);
        textViewTestCount = (TextView) findViewById(R.id.textViewTestCount);
        textViewSuccessCount = (TextView) findViewById(R.id.textViewSuccessCount);
        textViewFailCount = (TextView) findViewById(R.id.textViewFailCount);
        editTextOpenTime = (EditText)findViewById(R.id.editTextOpenTimeValue);
        editTextOpenTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextStopTime = (EditText)findViewById(R.id.editTextCloseTimeValue);
        editTextStopTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextTestNum = (EditText)findViewById(R.id.editTextTestNum);
        editTextTestNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewIp = (TextView)findViewById(R.id.textViewIP);
        textViewIp.setText("等待获取IP地址");
        //editTextSSID = (EditText)findViewById(R.id.editTextSSID);
        //editTextPasswd = (EditText)findViewById(R.id.editTextPasswd);
        //ch1 = (RadioButton) findViewById(R.id.ch1);
        //ch2 = (RadioButton) findViewById(R.id.ch2);
        btnOpen.setEnabled(true);
        btnClose.setEnabled(false);
        stringBuffer = new StringBuffer();
        //wifiInfo = wifiAdmin.createWifiInfo(editTextSSID.getText().toString(), editTextPasswd.getText().toString(), 3);
        //wifiInfo = wifiAdmin.createWifiInfo("helloworld", "xtee915920", 3);

        wifiAdmin.openWifi();

    }

    private View.OnClickListener startOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //isping = ch2.isChecked();
            openTime = 1000*Integer.parseInt(editTextOpenTime.getText().toString());
            stopTime = 1000*Integer.parseInt(editTextStopTime.getText().toString());
            btnOpen.setEnabled(false);
            btnClose.setEnabled(true);
            editTextOpenTime.setEnabled(false);
            editTextStopTime.setEnabled(false);
            editTextTestNum.setEnabled(false);
            //ch1.setEnabled(false);
            //ch2.setEnabled(false);
            textViewTestCount.setText("进行第："+String.valueOf(testCount)+"次测试");
            textViewFailCount.setText("失败次数："+String.valueOf(failCount)+"次");
            textViewSuccessCount.setText("成功次数："+String.valueOf(successCount)+"次");
            sendWifiMessage(MSG_WIFI_OPENWIFI,0);
            registerWifiReciver();
            startFlag = true;
        }
    };


    private View.OnClickListener stopOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnOpen.setEnabled(true);
            btnClose.setEnabled(false);
            editTextOpenTime.setEnabled(true);
            editTextStopTime.setEnabled(true);
            editTextTestNum.setEnabled(true);
            //ch1.setEnabled(true);
            //ch2.setEnabled(true);
            startFlag = false;
            isFailed = false;
            isFinishScan = false;
            handler.removeCallbacksAndMessages(null);
            wifiAdmin.openWifi();
            textViewMess.setText("已停止测试！");
            unregisterReceiver(networkConnectChangedReceiver);
            testCount = 0;
            failCount = 0;
            successCount = 0;
        }
    };

    private void initWifiList() {
        wifiAdmin.startScan();
        StringBuilder str = wifiAdmin.lookUpScan();
        if(str==null){
            sendWifiMessage(MSG_WIFI_GETWIFILISTFAIL,100);
        }
        textViewMess.setText(str);
    }

    private void registerWifiReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        registerReceiver(networkConnectChangedReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        try {
            if(networkConnectChangedReceiver!=null){
                unregisterReceiver(networkConnectChangedReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    class NetworkConnectChangedReceiver extends BroadcastReceiver {
        Toast toast;

        private void setToast(String text){
            if (toast != null)
            {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else
            {
                toast = Toast.makeText(getApplication(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                LogManager.d(TAG, "onReceive action is " + action);
                if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// 这个监听wifi的打开与关闭，与wifi的连接无关
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            setToast("Wifi关闭");
                            LogManager.d(TAG, "wifi 关闭");
                            if(startFlag && testCount != 0){
                                if(isFailed!=true&&isFinishScan){
                                    successCount ++;
                                    isFinishScan = false;
                                }else {
                                    isFailed = false;
                                }
                                textViewSuccessCount.setText("成功次数："+String.valueOf(successCount)+"次");
                                handler.removeMessages(MSG_WIFI_CLOSEWIFIFAIL);
                                sendWifiMessage(MSG_WIFI_OPENWIFI, stopTime);
                            }
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            setToast("wifi打开");
                            LogManager.d(TAG, "wifi 打开");
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            break;
                    }
                }else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                    Log.d(TAG, "--------------------------->网络状态已经改变<----------------------------");
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                    if(info != null && info.isAvailable() ) {
                        boolean isWifiType = info.getType() == ConnectivityManager.TYPE_WIFI;
                        Log.d(TAG, "当前网络类型：" + info.getType());
                        if (isWifiType) {
                            String name = info.getTypeName();
                            String ip = wifiAdmin.getIPAddress();
                            Log.d(TAG, "当前网络名称：" + name +";ip=" + ip);
                            if(startFlag){
                                handler.removeMessages(MSG_WIFI_OPENWIFIFAIL);
                                if("0.0.0.0".equals(ip)) {
                                    sendWifiMessage(MSG_WIFI_STARTSCAN, openTime);
                                    sendWifiMessage(MSG_WIFI_WAITMESSAGE, 4000);
                                }else {
                                    sendWifiMessage(MSG_WIFI_STARTSCAN, 0);
                                }

                            }
                        }
                    } else {
                        Log.d(TAG, "没有可用网络");
                    }
                }
            }
        }
    }

    public boolean ping(String host, int pingCount, StringBuffer stringBuffer) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        String command = "ping -c " + pingCount + " -w 100 " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.d(TAG, "ping: null");
                append(stringBuffer, "ping fail:process is null.");
                return false;
            }int status = process.waitFor();
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                append(stringBuffer, line);
            }

            if (status == 0) {
                Log.d(TAG, "ping: true");
                isSuccess = true;
            } else {
                Log.d(TAG, "ping: false");
                isSuccess = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    private void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
    }
}
