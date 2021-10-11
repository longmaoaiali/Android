package com.cvte.taobaounion.utils;

import android.content.Context;

/**
 * Created by user on 2020/10/28.
 */

public class SizeUtils {
    public static int dip2px(Context context,float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale*0.5f);
    }
}
