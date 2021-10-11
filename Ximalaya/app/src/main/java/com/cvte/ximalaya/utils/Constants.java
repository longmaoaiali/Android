package com.cvte.ximalaya.utils;

/**
 * Created by user on 2020/9/3.
 */

/*提供一些常量数据*/
public class Constants {

    /*获取推荐列表的专辑数量*/
    public static int RECOMMAND_COUNT = 20;

    //数据库相关的常量
    public static final String DB_NAME = "ximalaya.db";
    public static final int DB_VERSION_CODE = 1;
    //数据库sub table 字段
    public static final String SUB_TB_NAME = "subTb";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVERURL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_PLAYCOUNT = "playCount";
    public static final String SUB_TRACKSCOUNT = "tracksCount";
    public static final String SUB_AUTHORNAME = "authorName";
    public static final String SUB_ALBUMID = "albumId";

    public static final String HISTORY_TB_NAME = "historyTb";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "trackId";
    public static final String HISTORY_TRACK_title = "trackTitle";
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";
    public static final String HISTORY_DURATION = "historyDuration";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";



    /*"create table subTb(" +
                "_id integer," +
                "coverUrl varchar," +
                "title varchar," +
                "description varchar," +
                "playCount integer," +
                "tracksCount integer," +
                "authorName varchar," +
                "albumId integer);";*/

}
