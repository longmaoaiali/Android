package com.person.wifitest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WifiTest";
    private static final String TestHost = "www.baidu.com";

    private StringBuffer stringBuffer;

    //ui compant
    private Button btnStart;
    private Button btnStop;
    private TextView textViewTestCount;
    private TextView textViewTestSuccessCount;
    private TextView textViewTestFailedCount;
    private TextView textViewIP;
    private EditText editTextOpenTime;
    private EditText editTextCloseTime;
    private EditText editTextTestNum;
    //wifi
    private WifiAdmin mWifiAdmin;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    private int openTime = 0;
    private int closeTime = 0;
    private int testNums = 1000;//默认测试1000次
    private int testCount = 1;
    private int failCount = 0;
    private int successCount = 0;

    class NetworkConnectChangedReceiver extends BroadcastReceiver {
        Toast toast;

        private void setToastAndShow(String text){
            if(toast!=null){
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //todo:ConnectivityManager state change
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action)){
                LogManager.d(TAG,"receive action is "+action);
                if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                    //WIFI on off状态的改变
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);
                    switch (wifiState){
                        case WifiManager.WIFI_STATE_DISABLED:
                            setToastAndShow("WIFI已关闭");
                            LogManager.d(TAG,"WIFI已关闭");
                            //判断是否需要继续测试
                            if(--testNums > 0){
                                //测试次数未达到继续测试
                                textViewTestSuccessCount.setText("成功次数:"+ String.valueOf(++successCount) +"次");
                                sendWifiMessage(MSG_WIFI_OPEN,closeTime);
                            }

                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            setToastAndShow("wifi已打开");
                            LogManager.d(TAG,"WIFI已打开");
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            break;
                    }
                } else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                    //网络状态 是否已经分配到IP地址
                    LogManager.d(TAG,"ConnectivityManager state changed");
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if(networkInfo!=null && networkInfo.isAvailable()){
                        //boolean isWifiType = ();
                        if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                            String name = networkInfo.getTypeName();
                            String ip = mWifiAdmin.getIPAddress();
                            LogManager.d(TAG,"已连接的SSID "+name+" ip = "+ip);
                            if("0.0.0.0".equals(ip)){
                                //未分配到IP地址

                            }else{
                                //已分配到IP地址 ping测试OK以后 关闭WIFI开始下一次测试

                                sendWifiMessage(MSG_WIFI_PINGBAIDU,0);
                            }
                        }
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //wifi init
        mWifiAdmin = new WifiAdmin(this);

        //ui init
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(startOnclickListener);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(stopOnclickListener);

        textViewTestCount = (TextView) findViewById(R.id.textViewTestCount);
        textViewTestSuccessCount = (TextView) findViewById(R.id.textViewSuccessCount);
        textViewTestFailedCount = (TextView) findViewById(R.id.textViewFailCount);
        textViewIP = (TextView) findViewById(R.id.textViewIP);
        textViewIP.setText("waiting for connect");

        editTextOpenTime = (EditText) findViewById(R.id.editTextOpenTimeValue);
        editTextOpenTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextCloseTime = (EditText) findViewById(R.id.editTextCloseTimeValue);
        editTextCloseTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextTestNum = (EditText) findViewById(R.id.editTextTestNum);
        editTextTestNum.setInputType(InputType.TYPE_CLASS_NUMBER);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        stringBuffer = new StringBuffer();

        //进入应用直接打开WIFI
        mWifiAdmin.openWifi();
    }


    private View.OnClickListener startOnclickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //todo: start test
            openTime = 1000*Integer.parseInt(editTextOpenTime.getText().toString());
            closeTime = 1000*Integer.parseInt(editTextCloseTime.getText().toString());
            testNums = Integer.parseInt(editTextTestNum.getText().toString());
            LogManager.d(TAG,"openTime "+openTime+" ms");
            LogManager.d(TAG,"closeTime "+closeTime+" ms");
            LogManager.d(TAG,"testNums "+testNums);

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            editTextOpenTime.setEnabled(false);
            editTextCloseTime.setEnabled(false);
            editTextTestNum.setEnabled(false);

            textViewTestCount.setText("正在进行第: "+String.valueOf(testCount)+"次测试");
            textViewTestFailedCount.setText("失败次数: "+String.valueOf(failCount)+"次");
            textViewTestSuccessCount.setText("成功次数: "+String.valueOf(successCount)+"次");

            //todo: send message handler and register wifi status receiver
            sendWifiMessage(MSG_WIFI_OPEN,0);
            registerWifiReceiver();
        }
    };

    private View.OnClickListener stopOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //todo: stop test
        }
    };



    private static final int MSG_WIFI_STARTSCAN=0;
    private static final int MSG_WIFI_OPEN=1;
    private static final int MSG_WIFI_CLOSE=2;
    private static final int MSG_WIFI_PINGBAIDU=3;

    private void sendWifiMessage(int msg,int second){
        Message message = new Message();
        message.what = msg;
        handler.sendMessageDelayed(message,second);
    }

    private void registerWifiReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        registerReceiver(networkConnectChangedReceiver,filter);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_WIFI_STARTSCAN:

                    //todo:start scan
                    break;
                case MSG_WIFI_OPEN:
                    //todo:open wifi
                    LogManager.d(TAG,"open wifi");
                    break;
                case MSG_WIFI_CLOSE:
                    //todo:close wifi
                    LogManager.d(TAG,"close wifi");
                case MSG_WIFI_PINGBAIDU:
                    //todo: ping baidu
                    boolean status = false;
                    status = ping(TestHost,2);
                    if(status){
                        successCount++;
                        textViewTestSuccessCount.setText("成功次数: "+String.valueOf(failCount)+"次");
                    }else{
                        failCount++;
                        textViewTestFailedCount.setText("失败次数: "+String.valueOf(failCount)+"次");
                    }
                default:
                    break;
            }
        }
    };

    public boolean ping(String host,int pingCount){
        //String line = null;
        Process process = null;
        String command = "ping -c "+pingCount+" -w 100 "+host;
        boolean isSuccess = false;

        try {
            process = Runtime.getRuntime().exec(command);
            if(process == null){
                LogManager.e(TAG,"ping command error: "+command);
                return false;
            }

            int status = process.waitFor();
            if(status == 0){
                LogManager.d(TAG,"ping result OK");
                isSuccess = true;
            }else{
                LogManager.d(TAG,"ping result fail");
                isSuccess = false;
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(process != null){
                process.destroy();
            }
        }

        return isSuccess;
    }



}