package com.cvte.wifip2pfiletransfer.Base;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.widget.Toast;

import com.cvte.wifip2pfiletransfer.view.LoadingDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by user on 2020/11/2.
 */

public class BaseActivity extends AppCompatActivity {

    private LoadingDialog mLoadingDialog;
    private static Toast sToast;

    protected void setActionTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }


    protected void showLoadingDialog(String message){
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show(message,true,false);
    }

    protected void dismissLoadingDialog(){
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    protected <T extends Activity> void startActivity(Class<T> tClass){
        startActivity(new Intent(this,tClass));
    }

    protected <T extends Service> void startService(Class<T> tClass){
        startService(new Intent(this,tClass));
    }

    protected void showToast(String message){
        if (sToast == null) {
            sToast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }
}