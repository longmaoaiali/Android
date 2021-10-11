package com.cvte.taobaounion.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.cvte.taobaounion.utils.LogUtils;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

/**
 * Created by user on 2020/10/30.
 */

public class TbNestedScrollView extends NestedScrollView {

    public static final String TAG = "TbNestedScrollView";
    private int mHeaderHeight = 530;
    private int originScroll;

    public TbNestedScrollView(Context context) {
        super(context);
    }

    public TbNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TbNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
//                               int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
//        super.onNestedScroll(target,dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed, type, consumed);
//        LogUtils.d(TAG,"customer TbNestedScrollView");
//    }

    public void setHeaderHeight(int headerHeight){
        this.mHeaderHeight = headerHeight;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,
                                  @ViewCompat.NestedScrollType int type) {
        //dy 垂直水平滑动距离
        if(originScroll < mHeaderHeight){
            scrollBy(dx,dy);
            consumed[0] = dx;
            consumed[1] = dy;
        }

        super.onNestedPreScroll(target,dx,dy,consumed,type);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        this.originScroll = t;
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
