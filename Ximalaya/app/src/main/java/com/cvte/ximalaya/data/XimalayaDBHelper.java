package com.cvte.ximalaya.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cvte.ximalaya.utils.Constants;
import com.cvte.ximalaya.utils.LogUtil;

/**
 * Created by user on 2020/10/12.
 */

public class XimalayaDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "XimalayaDBHelper";

    public XimalayaDBHelper(Context context) {
        //name是数据库的名称 factory游标 version数据库版本
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG,"onCreate ");
        //创建数据库表, 订阅相关的字段
        //图片 title 描述 播放量 节目数量 作者名称 专辑id(详情界面需要)
        String subTbSql = "create table subTb(" +
                "_id integer primary key autoincrement," +
                "coverUrl varchar," +
                "title varchar," +
                "description varchar," +
                "playCount integer," +
                "tracksCount integer," +
                "authorName varchar," +
                "albumId integer);";

        db.execSQL(subTbSql);

        //插入数据 测试数据库
//        String insertSQLTest = "insert into subTb(title,description) values('title','description');";
//        for (int i = 0; i < 1000; i++) {
//            db.execSQL(insertSQLTest);
//        }

        String historyTbSql = "create table historyTb(" +
                "_id integer primary key autoincrement," +
                "trackId integer," +
                "trackTitle varchar," +
                "historyCover varchar," +
                "historyPlayCount integer," +
                "historyDuration integer," +
                "historyUpdateTime integer);";

        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
