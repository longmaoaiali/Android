package com.cvte.androidnetwork;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cvte.androidnetwork.domain.CommentItem;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

public class RequestTestActivity extends AppCompatActivity {
    private static final String TAG="RequestTestActivity";

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

    public void postFile(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File("/XXX/XXX/1.jpg");
                String fileName = file.getName();
                String BOUNDARY = "--------------------------615699136952189685578639";
                String KEY = "file";
                OutputStream outputStream = null;

                try {
                    /*抓包的请求行*/
                    //POST http://192.168.1.100:9102/file/upload HTTP/1.1
                    /*抓包的 HEAD信息*/
                    //User-Agent: PostmanRuntime/7.26.3
                    //Accept: */*
                    //Postman-Token: c8e3be46-a6d9-4917-a9e6-072bc56f87f1
                    //Host: 192.168.1.100:9102
                    //Accept-Encoding: gzip, deflate, br
                    //Connection: keep-alive
                    //Content-Type: multipart/form-data; boundary=--------------------------615699136952189685578639
                    //Content-Length: 36328
                    //
                    /*抓包的 TEXT文本信息 包含图片的头部数据与二进制数据*/
                    //----------------------------615699136952189685578639
                    //Content-Disposition: form-data; name="file"; filename="1.jpg"
                    //Content-Type: image/jpeg
                    //
                    /*图片的二进制数据*/
                    //�����C�
                    URL url = new URL("http://10.0.2.2:9102/file/upload");
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setRequestProperty("User-Agent","Android/"+ Build.VERSION.SDK_INT);
                    httpURLConnection.setRequestProperty("Accept","*/*");
                    httpURLConnection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
                    httpURLConnection.setRequestProperty("Connection","keep-alive");
                    httpURLConnection.setRequestProperty("Content-Type","multipart/form-data; boundary="+BOUNDARY);

                    httpURLConnection.connect();

                    /*准备图片的头部数据与二进制数据*/
                    StringBuilder SbInfo = new StringBuilder();
                    SbInfo.append("--");
                    SbInfo.append(BOUNDARY);
                    SbInfo.append("\r\n");
                    SbInfo.append("Content-Disposition: form-data; name=\"" + KEY + "\"; filename=\""+fileName+"\"");
                    SbInfo.append("\r\n");
                    SbInfo.append("Content-Type: image/jpeg");
                    SbInfo.append("\r\n");
                    SbInfo.append("\r\n");

                    outputStream=httpURLConnection.getOutputStream();
                    byte[] SbInfoByte = SbInfo.toString().getBytes("UTF-8");
                    outputStream.write(SbInfoByte);

                    /*文件内容*/
                    FileInputStream fos = new FileInputStream(file);
                    BufferedInputStream bfi = new BufferedInputStream(fos);
                    byte[] fileByte = new byte[1024];
                    int len;
                    while ((len=bfi.read(fileByte,0,fileByte.length)) != -1){
                        outputStream.write(fileByte,0,len);
                    }

                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
