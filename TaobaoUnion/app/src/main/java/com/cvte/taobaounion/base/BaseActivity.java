package com.cvte.taobaounion.base;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by user on 2020/11/3.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        /*++++++++++++++++++++++++++++++++++++*/
        /*ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        View contentContainer = getWindow().getDecorView();
        contentContainer.setLayerType(View.LAYER_TYPE_SOFTWARE,paint);*/
        /*++++++++++++++++++++++++++++++++++++*/
        mBind = ButterKnife.bind(this);
    }

    protected abstract int getLayoutResId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
