package com.cvte.player.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cvte.player.interfaces.IPlayControl;
import com.cvte.player.interfaces.IPlayViewControl;
import com.cvte.player.presenter.PlayerPresenter;

/**
 * Created by user on 2020/8/11.
 */

public class PlayerService extends Service {

    private static final String TAG ="PlayerService";
    private PlayerPresenter playerPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        if (playerPresenter == null) {
            playerPresenter = new PlayerPresenter();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return playerPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerPresenter = null;
    }
}
