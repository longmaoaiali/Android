package com.cvte.ximalaya.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * Created by user on 2020/9/1.
 */

public class BaseApplication extends Application{

    private static Handler sHandler = null;
    private static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        //SDK jar包中默认isRelease=true
        if(DTransferConstants.isRelease) {
            String mAppSecret = "afe063d2e6df361bc9f1fb8bb8210d67";
            mXimalaya.setAppkey("af1d317b871e0e7e2ce45872caa34d9a");
            mXimalaya.setPackid("com.humaxdigital.automotive.ximalaya");
            mXimalaya.init(this ,mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this ,mAppSecret);
        }

        //开发时时 这里选择true,将log开放
        LogUtil.init(this.getPackageName(), true);
        //初始化播放器
        XmPlayerManager.getInstance(this).init();

        sHandler = new Handler();
        sContext = getBaseContext();
    }

    public static Context getAppContext(){
        return sContext;
    }

    public static Handler getHandler() {
        return sHandler;
    }

}
