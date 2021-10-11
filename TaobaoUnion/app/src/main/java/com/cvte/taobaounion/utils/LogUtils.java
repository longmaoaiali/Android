package com.cvte.taobaounion.utils;

import android.util.Log;

/**
 * Created by user on 2020/10/21.
 */

public class LogUtils {
    private   static int currentLev = 4;
    private  static int debugLev = 4;
    private  static int infoLev = 3;
    private  static int warningLev = 2;
    private  static int errorLev = 1;

    public static void d(String TAG, String log){
        if (currentLev>= debugLev) {
            Log.d(TAG,log);
        }
    }

    public static void i(String TAG, String log){
        if (currentLev>= infoLev) {
            Log.i(TAG,log);
        }
    }

    public static void w(String TAG, String log){
        if (currentLev>= warningLev) {
            Log.w(TAG,log);
        }
    }

    public static void e(String TAG, String log){
        if (currentLev>= errorLev) {
            Log.e(TAG,log);
        }
    }

}
