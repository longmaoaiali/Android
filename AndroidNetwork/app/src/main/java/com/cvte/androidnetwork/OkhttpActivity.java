package com.cvte.androidnetwork;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cvte.androidnetwork.domain.CommentItem;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static java.net.HttpURLConnection.HTTP_OK;

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
                        if(code== HTTP_OK){
                            ResponseBody body = response.body();
                            Log.d(TAG,"body -->" + body.string());
                        }
                    }
                });
            }
        });

    }

    public void postRequest(View view) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000,TimeUnit.MILLISECONDS)
                .build();

        CommentItem commentItem = new CommentItem(1234,"赞赞");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(commentItem);
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(jsonStr,mediaType);

        final Request request = new Request.Builder()
                .url("http://10.0.2.2:9102/post/comment")
                .post(requestBody)
                .build();

       Call task = okHttpClient.newCall(request);
       task.enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               Log.d(TAG,"post failed");
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                Log.d(TAG,"response code"+ code);
                if(code == HTTP_OK){
                    ResponseBody body = response.body();
                    if(body != null){
                        Log.d(TAG,"response result--> " + body.string());
                    }
                }
           }
       });
    }

    public void postFile(View view) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000,TimeUnit.MILLISECONDS)
                .build();

        File file = new File("1.png");

        MediaType fileType = MediaType.parse("image/png");
        RequestBody fileBody = RequestBody.create(file,fileType);

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file",file.getName(),fileBody)
                .build();

        final Request request = new Request.Builder()
                .url("http://10.0.2.2:9102/file/upload")
                .post(requestBody)
                .build();

        Call task = okHttpClient.newCall(request);
        /*同步方法 task.execute(); 创建一个子线程，将其丢在里面进行实现*/
        task.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                Log.d(TAG,"code ==== >" + code);
                if(code == HTTP_OK){
                    ResponseBody body = response.body();
                    if(body != null){
                        Log.d(TAG,"result --> "+body.string());
                    }
                }
            }
        });
    }
}