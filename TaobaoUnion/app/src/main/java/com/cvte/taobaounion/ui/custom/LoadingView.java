package com.cvte.taobaounion.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.cvte.taobaounion.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by user on 2020/11/4.
 */

public class LoadingView extends AppCompatImageView {

    private float mDegrees = 0;
    private boolean mNeedRotate = true;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegrees,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();
    }

    private void startRotate() {
        post(new Runnable() {
            @Override
            public void run() {
                mDegrees += 10;
                if (mDegrees>360) {
                    mDegrees = 0;
                }
                invalidate();
                if(getVisibility() != VISIBLE && !mNeedRotate){
                    removeCallbacks(this);
                }else{
                    postDelayed(this,10);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
    }
}
