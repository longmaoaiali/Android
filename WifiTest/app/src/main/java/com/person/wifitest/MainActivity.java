package com.person.wifitest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private int testCount = 1;
    private int failCount = 0;
    private int successCount = 0;

    class NetworkConnectChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //todo:ConnectivityManager state change
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
                    Log.d(TAG,"open wifi");
                    break;
                default:
                    break;
            }
        }
    };

}