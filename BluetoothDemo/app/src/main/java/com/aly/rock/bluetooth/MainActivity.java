package com.aly.rock.bluetooth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;


public class MainActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_scanDevice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BleActivity.class);
            }
        });
        findViewById(R.id.btn_connectA2dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ConnectA2dpActivity.class);
            }
        });
        findViewById(R.id.btn_recycle_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecycleActivity.class);
            }
        });
        findViewById(R.id.btn_audio_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AudioRecordActivity.class);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSelfPermission();
    }

    private void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        showToast("请授予权限");
                        finish();
                        return;
                    }
                }
                break;
        }
    }

}