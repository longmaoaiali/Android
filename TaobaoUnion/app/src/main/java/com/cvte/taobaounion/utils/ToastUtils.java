package com.cvte.taobaounion.utils;

import android.widget.Toast;

import com.cvte.taobaounion.base.BaseApplication;

/**
 * Created by user on 2020/10/29.
 */

public class ToastUtils {

    private static Toast sToast;


    public static void showToast(String tips){
        if (sToast == null) {
            sToast = Toast.makeText(BaseApplication.getAppContext(),tips,Toast.LENGTH_SHORT);
        } else {
            sToast.setText(tips);
        }
        sToast.show();
    }

}
