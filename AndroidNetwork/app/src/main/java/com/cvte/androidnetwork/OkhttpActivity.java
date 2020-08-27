package com.cvte.androidnetwork;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by user on 2020/8/27.
 */

public class OkhttpActivity extends AppCompatActivity {
    private static final String TAG="OkhttpActivity";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);

        ininView();
    }

    private void ininView() {
        Button getRequestButton = this.findViewById(R.id.getRequest);
        getRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*客户端*/
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10000, TimeUnit.MILLISECONDS)
                        .build();

                /*创建请求内容*/
                Request request = new Request.Builder()
                        .get()
                        .url("http://10.0.2.2:9102/get/text")
                        .build();

                Call task = okHttpClient.newCall(request);
                //异步请求
                task.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG,"onFailure-->"+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int code = response.code();
                        Log.d(TAG,"response code" + code);
                        if(code== HttpURLConnection.HTTP_OK){
                            ResponseBody body = response.body();
                            Log.d(TAG,"body -->" + body.string());
                        }
                    }
                });
            }
        });

    }
}