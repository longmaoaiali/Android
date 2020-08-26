package com.cvte.androidnetwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PicLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_load);
    }

    public void loadPic(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://cdn.sunofbeaches.com/images/test/1.jpg");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
                    connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9");
                    connection.setRequestProperty("Accept","application/json, text/plain, */*");
                    connection.setRequestProperty("Connection","keep-alive");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();

                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //UI Thread 更新UI界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = findViewById(R.id.result_image);
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
