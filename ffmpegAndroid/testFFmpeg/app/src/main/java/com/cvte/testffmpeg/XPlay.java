package com.cvte.testffmpeg;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by user on 2020/12/17.
 */

public class XPlay extends SurfaceView implements Runnable,SurfaceHolder.Callback{
    public XPlay(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public XPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }
//    public XPlay(Context context, AttributeSet attributeSet) {
//        super(context,attributeSet);
//    }



    @Override
    public void run() {
        open("/sdcard/1080.mp4",getHolder().getSurface());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public native void open(String url,Object surface);
}
