package com.cvte.ximalaya.utils;

/**
 * Created by user on 2020/9/1.
 */
import android.util.Log;

public class LogUtil {
    public static String sTAG = "LogUtil";
    //是调试模式吗？默认输出log
    public static boolean sIsDebug = true;

    public static void init(String baseTag,boolean isDebug){
        sTAG = baseTag;
        sIsDebug = isDebug;
    }

    public static void d(String TAG,String content){
        if(sIsDebug){
            /*应用名+类名+日志内容*/
            Log.d("["+sTAG+"]"+TAG,content);
        }
    }

    public static void v(String TAG,String content){
        if(sIsDebug){
            Log.v("["+sTAG+"]"+TAG,content);
        }
    }

    public static void i(String TAG,String content){
        if(sIsDebug){
            Log.i("["+sTAG+"]"+TAG,content);
        }
    }

    public static void w(String TAG,String content){
        if(sIsDebug){
            Log.w("["+sTAG+"]"+TAG,content);
        }
    }

    public static void e(String TAG,String content){
        if(sIsDebug){
            Log.e("["+sTAG+"]"+TAG,content);
        }
    }

}
