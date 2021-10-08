package com.cvte.player.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.cvte.player.MainActivity;
import com.cvte.player.interfaces.IPlayControl;
import com.cvte.player.interfaces.IPlayViewControl;
import com.cvte.player.services.PlayerService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2020/8/11.
 */

public class PlayerPresenter extends Binder implements IPlayControl {
    private static final String TAG = "PlayerPresenter";
    private IPlayViewControl viewControl;
    private int currentState = PLAY_STATE_STOP;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private SeekTimeTask seekTimeTask;

    @SuppressLint("SdCardPath")
    @Override
    public void playOrPause() throws IOException {
        Log.d(TAG, "playOrPause");
        if(currentState == PLAY_STATE_STOP){
            initPlayer();
            mediaPlayer.setDataSource("/mnt/sdcard/song.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentState = PLAY_STATE_PLAYER;
            startTimer();
            Log.d(TAG, "mediaPlayer.start");
        } else if(currentState == PLAY_STATE_PLAYER){
            if(mediaPlayer != null){
                mediaPlayer.pause();
                currentState = PLAY_STATE_PAUSE;
                stopTimer();
                Log.d(TAG, "mediaPlayer.pause");
            }
        } else if(currentState == PLAY_STATE_PAUSE){
            if(mediaPlayer != null){
                mediaPlayer.start();
                currentState = PLAY_STATE_PLAYER;
                startTimer();
            }
        }

        //服务将当前状态更新在UI上面
        if (viewControl != null) {
            viewControl.onPlayerStateChange(currentState);
        }

    }



    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            currentState = PLAY_STATE_STOP;
            stopTimer();
            if (viewControl != null) {
                viewControl.onPlayerStateChange(currentState);
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void seekTo(int seek) {
        Log.d(TAG, "seekTo"+seek);
        if(mediaPlayer != null) {
            int tarSeek = (int)(seek * 1.0f / 100 * mediaPlayer.getDuration());
            mediaPlayer.seekTo(tarSeek);
        }

    }

    @Override
    public void registerViewController(IPlayViewControl viewController) {
        this.viewControl = viewController;
    }

    @Override
    public void unRegisterViewController() {
        this.viewControl = null;
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (seekTimeTask == null) {
            seekTimeTask = new SeekTimeTask();
        }
        timer.schedule(seekTimeTask,0,500);
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (seekTimeTask != null) {
            seekTimeTask.cancel();
            seekTimeTask = null;
        }

    }

    private class SeekTimeTask extends TimerTask {
        @Override
        public void run() {
            if(mediaPlayer != null && viewControl != null){
                int currentPosition = mediaPlayer.getCurrentPosition()*100/mediaPlayer.getDuration();
                Log.d(TAG,"current play position..." + currentPosition);
                viewControl.onSeekChange(currentPosition);
            }
        }
    }
}
