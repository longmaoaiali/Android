package com.cvte.player;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.cvte.player.interfaces.IPlayControl;
import com.cvte.player.interfaces.IPlayViewControl;
import com.cvte.player.services.PlayerService;

import java.io.IOException;

import static com.cvte.player.interfaces.IPlayControl.PLAY_STATE_PAUSE;
import static com.cvte.player.interfaces.IPlayControl.PLAY_STATE_PLAYER;
import static com.cvte.player.interfaces.IPlayControl.PLAY_STATE_STOP;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SeekBar seekBar;
    private Button playOrPause;
    private Button closeBt;
    private PlayerConnection playerConnection;
    private IPlayControl iPlayControl;

    //用户的手是否在进度条上面
    private boolean isUserTouchProgressBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSdCardPermisssion();
        initView();
        initViewEvent();

        //开启服务与绑定服务
        initService();
        initBindService();
    }

    private void requestSdCardPermisssion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            Log.d(TAG,"您已经申请了权限!");
        }
    }

    private void initService() {
        Log.d(TAG, "startService-->");
        startService(new Intent(MainActivity.this,PlayerService.class));
    }

    private void initBindService() {
        Log.d(TAG, "initBindService-->");
        Intent intent = new Intent(this, PlayerService.class);
        playerConnection = new PlayerConnection();
        bindService(intent,playerConnection,BIND_AUTO_CREATE);

    }

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected-->"+service);
            iPlayControl = (IPlayControl)service;
            //将界面控制 iPlayViewControl 给到服务端 iPlayControl
            iPlayControl.registerViewController(iPlayViewControl);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iPlayControl = null;
        }
    }

    //service 通过IPlayViewControl 的接口来控制UI变化
    private IPlayViewControl iPlayViewControl = new IPlayViewControl() {
        @Override
        public void onPlayerStateChange(int state) {
            switch (state){
                case PLAY_STATE_PLAYER:
                    //播放时按钮改为暂停
                    playOrPause.setText("暂停");
                    break;
                case PLAY_STATE_PAUSE:
                    playOrPause.setText("播放");
                    break;
                case PLAY_STATE_STOP:
                    playOrPause.setText("播放");
                    break;
            }
        }

        @Override
        public void onSeekChange(final int seek) {
            Log.d(TAG, "current thread --> "+Thread.currentThread().getName());
            //用户手放在进度条上面时，不更新进度条避免抖动
            //子线程不可以更新UI,但是progressBar 与 surfaceView 控件可以在子线程更新
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isUserTouchProgressBar) {
                        seekBar.setProgress(seek);
                    }
                }
            });

        }
    };

    private void initViewEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条发生改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
                isUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动
                isUserTouchProgressBar = false;
                int touchProgress = seekBar.getProgress();
                Log.d(TAG, "touchProgress-->" + touchProgress);

                if (iPlayControl != null) {
                    iPlayControl.seekTo(touchProgress);
                }
            }
        });


        playOrPause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //播放或者暂停
                if (iPlayControl != null) {
                    try {
                        iPlayControl.playOrPause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iPlayControl != null) {
                    iPlayControl.stop();
                }
            }
        });
    }

    private void initView() {
        seekBar = (SeekBar) this.findViewById(R.id.seek);
        playOrPause = (Button) this.findViewById(R.id.play_or_pause);
        closeBt = (Button) this.findViewById(R.id.close_bt);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playerConnection != null){

            iPlayControl.unRegisterViewController();
            unbindService(playerConnection);
        }
    }
}
