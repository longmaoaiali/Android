package com.cvte.wifip2pfiletransfer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.cvte.wifip2pfiletransfer.Utils.Md5Util;
import com.cvte.wifip2pfiletransfer.common.Constants;
import com.cvte.wifip2pfiletransfer.model.FileTransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by user on 2020/11/11.
 * IntentService 做耗时操作传输文件
 */

public class WifiServerService extends IntentService {

    private static final String TAG = "WifiServerService";
    private ServerSocket mServerSocket;
    private InputStream mClientInputStream;
    private ObjectInputStream mObjectInputStream;
    private FileOutputStream mFileOutputStream;
    private OnProgressChangListener mOnProgressChangListener = null;


    public WifiServerService(){
        super("WifiServerService");
    }

    /*Binder*/
    public class MyBinder extends Binder {
        public WifiServerService getService() {
            return WifiServerService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WifiServerService(String name) {
        super(name);
    }

    public void setOnProgressChangListener(OnProgressChangListener listener){
        this.mOnProgressChangListener = listener;
    }

    public interface OnProgressChangListener{
        //进度条变化
        void onProgressChanged(FileTransfer fileTransfer,int progress);

        //文件传输完成
        void  onTransferFinished(File file);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"FileService IntentService onHandleIntent");
        File file = null;
        try {
            //socket网络编程
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(Constants.PORT));
            /*建立客户端的连接*/
            Socket client = mServerSocket.accept();
            Log.d(TAG,"client IP Address-->" + client.getInetAddress().getHostAddress());

            mClientInputStream = client.getInputStream();
            mObjectInputStream = new ObjectInputStream(mClientInputStream);
            FileTransfer fileTransfer = (FileTransfer) mObjectInputStream.readObject();
            Log.d(TAG,"传输的文件信息 -->" + fileTransfer.toString());

            //得到文件并指定文件存储位置
            String name = new File(fileTransfer.getFilePath()).getName();
            Log.d(TAG,Environment.getExternalStorageDirectory()+"/"+name);
            file = new File(Environment.getExternalStorageDirectory()+"/"+name);

            //将文件保存至指定路径
            mFileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[512];
            int len;
            long total = 0;
            int progress;
            while ((len = mClientInputStream.read(buf)) != -1) {
                mFileOutputStream.write(buf,0,len);
                total += len;
                progress = (int) ((total*100)/fileTransfer.getFileLength());
                Log.d(TAG,"文件传输进度： "+progress);
                if (mOnProgressChangListener != null) {
                    mOnProgressChangListener.onProgressChanged(fileTransfer,progress);
                }
            }

            //文件传输完成,资源释放
            releaseTransFile();
            Log.d(TAG,"文件MD5 SUM --> " + Md5Util.getMd5(file));

        } catch (Exception e) {
            Log.d(TAG,"File Transfer Exception");
            e.printStackTrace();
        } finally {
            clean();
            if (mOnProgressChangListener != null) {
                mOnProgressChangListener.onTransferFinished(file);
            }
            //重启自己服务，便于下次连接
            startService(new Intent(this,WifiServerService.class));
        }
    }

    private void clean() {
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
                mServerSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mClientInputStream != null) {
            try {
                mClientInputStream.close();
                mClientInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mObjectInputStream != null) {
            try {
                mObjectInputStream.close();
                mObjectInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
                mFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void releaseTransFile() {
        try {
            mServerSocket.close();
            mClientInputStream.close();
            mObjectInputStream.close();
            mFileOutputStream.close();

            mServerSocket = null;
            mClientInputStream = null;
            mObjectInputStream = null;
            mFileOutputStream = null;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clean();
    }
}
