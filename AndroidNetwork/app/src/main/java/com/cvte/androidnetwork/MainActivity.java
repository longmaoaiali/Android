package com.cvte.androidnetwork;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cvte.androidnetwork.adapters.GetResultListAdapter;
import com.cvte.androidnetwork.domain.GetTextItem;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";
    private static final int REQUESTCODE = 1;
    private GetResultListAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNetPermission();
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = this.findViewById(R.id.result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //每个Item添加分隔
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });

       mAdapter = new GetResultListAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkNetPermission() {
        Log.d(TAG,"checkNetPermission");
        int internetPermission = checkSelfPermission(Manifest.permission.INTERNET);
        int networkPermission = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        Log.d(TAG, "internetPermission = "+internetPermission);
        Log.d(TAG, "networkPermission = "+networkPermission);

        if((internetPermission != PackageManager.PERMISSION_GRANTED) ||
                (networkPermission != PackageManager.PERMISSION_GRANTED)){
            Log.d(TAG, "request permission");
            requestPermissions(new String[]{Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE},REQUESTCODE);

        } else {
            Log.d(TAG,"Granted "+Manifest.permission.INTERNET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((requestCode == REQUESTCODE) && (grantResults[0]==PackageManager.PERMISSION_GRANTED) && (grantResults[1]==PackageManager.PERMISSION_GRANTED)){
            Log.d(TAG,"Request " + Manifest.permission.INTERNET + " Granted ");
        }
    }

    public void loadJson(View view) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:9102/get/text");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);//设置连接超时
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
                    connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9");
                    connection.setRequestProperty("Accept","application/json, text/plain, */*");
                    connection.setRequestProperty("Connection","keep-alive");

                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode == 200){
                        Map<String, List<String>> headerFields = connection.getHeaderFields();
                        Set<Map.Entry<String,List<String>>> entries = headerFields.entrySet();
                        for(Map.Entry<String,List<String>> entry:entries) {
                            Log.d(TAG,entry.getKey()+" == "+entry.getValue());
                        }

                        Object content = connection.getContent();
                        //Log.d(TAG,"content -->"+content);

                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                        String json;
                        while ((json =bufferedReader.readLine()) != null) {
                            Log.d(TAG,"readLine string is " + json);

                            //GSON
                            Gson gson = new Gson();
                            GetTextItem getTextItem = gson.fromJson(json, GetTextItem.class);

                            uodateUI(getTextItem);
                        }
                        bufferedReader.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void uodateUI(final GetTextItem getTextItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setData(getTextItem);
            }
        });
    }
}
