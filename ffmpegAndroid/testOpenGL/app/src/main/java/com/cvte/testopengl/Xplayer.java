package com.cvte.testopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by user on 2020/12/19.
 */

public class Xplayer extends SurfaceView implements Runnable,SurfaceHolder.Callback {

    public Xplayer(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public Xplayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Xplayer","liuyu surfaceCreated");
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {
        Log.d("Xplayer","run");
        open("/sdcard/out.yuv",getHolder().getSurface());
    }

    public native void open(String url,Object surface);
}
