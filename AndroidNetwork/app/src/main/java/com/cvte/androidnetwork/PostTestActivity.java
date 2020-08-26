package com.cvte.androidnetwork;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cvte.androidnetwork.domain.CommentItem;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by user on 2020/8/26.
 */

public class PostTestActivity extends AppCompatActivity {
    private static final String TAG="PostTestActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    public void postRequest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL("http://10.0.2.2:9102/post/comment");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Accept-Encoding", " gzip, deflate, br");
                    httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    CommentItem commentItem = new CommentItem(123, "这是提交的评价内容");
                    Gson gson = new Gson();
                    //post 需要构造一个json 字符对象
                    String jsonStr = gson.toJson(commentItem);
                    byte[] bytes = jsonStr.getBytes();
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(bytes.length));

                    Log.d(TAG,"will call httpURLConnection.connect");
                    //连接
                    httpURLConnection.connect();
                    //把数据给到服务
                    outputStream = httpURLConnection.getOutputStream();

                    outputStream.write(bytes);
                    outputStream.flush();

                    //响应结果
                    int responseCode = httpURLConnection.getResponseCode();
                    Log.d(TAG,"responseCode is " + responseCode);
                    if (responseCode == HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        Log.d(TAG, "result --> " + bufferedReader.readLine());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }

    public void getWithParam(View view) {

        Log.d(TAG,"button click --> getWithParam");
        Map<String,String> params = new HashMap<>();
        params.put("keyword","这是我的关键词Keyword");
        params.put("page","12");
        params.put("order","0");

        startRequest(params,"GET","/get/param");
    }

    private void startRequest(final Map<String, String> params, final String method, final String api) {
        Log.d(TAG,"startRequest");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null;
                try {

                    //组装参数 https://www.sunofbeach.net/search?keyword=%E5%BC%80%E6%BA%90%E6%A1%86%E6%9E%B6
                    StringBuilder sb = new StringBuilder();
                    if (params != null && params.size()>0) {
                        sb.append("?");
                        //迭代器取Map的数据
                        Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();

                        while (iterator.hasNext()){
                            Map.Entry<String,String> next = iterator.next();
                            sb.append(next.getKey());
                            sb.append("=");
                            sb.append(next.getValue());
                            if(iterator.hasNext()){
                                sb.append("&");
                            }
                        }
                        Log.d(TAG,"StringBuilder result "+sb.toString());
                    }

                    String params = sb.toString();
                    URL url;
                    if (params != null && params.length()>0) {
                        url = new URL("http://10.0.2.2:9102"+api+params);
                    } else{
                        url = new URL("http://10.0.2.2:9102"+api);
                    }
                    Log.d(TAG,"url result "+url.toString());


                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod(method);
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
                    httpURLConnection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9");
                    httpURLConnection.setRequestProperty("Accept","application/json, text/plain, */*");
                    httpURLConnection.setRequestProperty("Connection","keep-alive");
                    httpURLConnection.connect();

                    int responseCode = httpURLConnection.getResponseCode();
                    Log.d(TAG,"responseCode is " + responseCode);
                    if(responseCode == HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String json = bufferedReader.readLine();
                        Log.d(TAG,"result----> "+json);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }
}
