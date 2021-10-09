package com.cvte.wifip2pfiletransfer.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.cvte.wifip2pfiletransfer.Utils.Md5Util;
import com.cvte.wifip2pfiletransfer.common.Constants;
import com.cvte.wifip2pfiletransfer.model.FileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by user on 2020/11/19.
 */

public class WifiClientTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "WifiClientTask";
    private final FileTransfer mFileTransfer;
    private final ProgressDialog mProgressDialog;

    //strings[0]表示socket 地址对应String   AsyncTask<String, Integer, Boolean>
    @Override
    protected Boolean doInBackground(String... strings) {
        mFileTransfer.setMD5(Md5Util.getMd5(new File(mFileTransfer.getFilePath())));
        Log.e(TAG,"文件的MD5码值: "+mFileTransfer.getMD5());
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;

        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(new InetSocketAddress(strings[0], Constants.PORT),10000);
            Log.d(TAG,strings[0]);
            Log.d(TAG,Constants.PORT+"");

            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(mFileTransfer);

            inputStream = new FileInputStream(new File(mFileTransfer.getFilePath()));
            long fileSize = mFileTransfer.getFileLength();
            long total = 0;
            byte[] buf = new byte[512];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                //客户端将文件数据写入socket
                outputStream.write(buf,0,len);
                total += len;
                int progress = (int) ((total*100)/fileSize);
                publishProgress(progress);
                Log.d(TAG,"文件发送进度"+progress);
            }

            socket.close();
            inputStream.close();
            outputStream.close();
            objectOutputStream.close();
            socket = null;
            inputStream = null;
            outputStream = null;
            objectOutputStream = null;
            Log.d(TAG,"文件传输完成");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public WifiClientTask(Context context, FileTransfer fileTransfer) {
        this.mFileTransfer = fileTransfer;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在发送文件");
        mProgressDialog.setMax(100);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    //执行完doInBackground执行，run on UI Thread
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mProgressDialog.cancel();
        Log.d(TAG,"run on UI Thread");
    }

    //Runs on the UI thread after {@link #publishProgress}  in doInBackground
    @Override
    protected void onProgressUpdate(Integer... values) {
        mProgressDialog.setProgress(values[0]);
    }
}
