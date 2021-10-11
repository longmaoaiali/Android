package com.cvte.ximalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by user on 2020/9/3.
 */

public class RoundRectImageView extends AppCompatImageView {
    private float roundRatio = 0.1f;
    private Path mPath;

    public RoundRectImageView(Context context) {
        super(context);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPath != null) {
            mPath = new Path();
            mPath.addRoundRect(new RectF(0,0,getWidth(),getHeight()),roundRatio*getWidth(),roundRatio*getHeight(), Path.Direction.CW);
        }
        canvas.save();
        if (mPath != null) {
            canvas.clipPath(mPath);
        }
        super.onDraw(canvas);
        canvas.restore();
    }
}
