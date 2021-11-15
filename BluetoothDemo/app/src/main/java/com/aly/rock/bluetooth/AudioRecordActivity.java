package com.aly.rock.bluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.aly.rock.bluetooth.audio.AudioCapturer;
import com.aly.rock.bluetooth.audio.AudioPlayer;

public class AudioRecordActivity extends AppCompatActivity implements AudioCapturer.OnAudioFrameCapturedListener {
    private static final String TAG = "MainActivity";
    private AudioCapturer mAudioCapturer;
    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_capture);
        startCapturer();
        startPlayer();
    }

    private void startCapturer() {
        mAudioCapturer = new AudioCapturer();
        mAudioCapturer.setOnAudioFrameCapturedListener(this);
        mAudioCapturer.startCapture();
    }

    private void startPlayer() {
        mAudioPlayer = new AudioPlayer();
        mAudioPlayer.startPlayer();
    }

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {
        if (mAudioPlayer != null) {
            mAudioPlayer.play(audioData, 0, audioData.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioCapturer != null) {
            mAudioCapturer.stopCapture();
        }

        if (mAudioPlayer != null) {
            mAudioPlayer.startPlayer();
        }
    }
}
