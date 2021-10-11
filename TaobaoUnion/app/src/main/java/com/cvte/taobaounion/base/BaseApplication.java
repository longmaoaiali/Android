package com.cvte.taobaounion.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by user on 2020/10/29.
 */

public class BaseApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getBaseContext();
    }

    public static Context getAppContext(){
        return appContext;
    }
}
