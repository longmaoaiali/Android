package com.cvte.taobaounion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cvte.taobaounion.base.BaseApplication;
import com.cvte.taobaounion.model.domain.CacheWithDuration;
import com.google.gson.Gson;

/**
 * Created by user on 2020/11/11.
 */

public class JsonCacheUtil {

    public static final String JSON_CACHE_SP_NAME = "json_cache_sp_name";
    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    private JsonCacheUtil() {
        mSharedPreferences = BaseApplication.getAppContext().getSharedPreferences(JSON_CACHE_SP_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    private static  JsonCacheUtil sJsonCacheUtil = null;

    public static JsonCacheUtil getInstance(){
        if (sJsonCacheUtil == null) {
            sJsonCacheUtil = new JsonCacheUtil();
        }
        return sJsonCacheUtil;
    }

    public void saveCache(String key,Object value){
        this.saveCache(key,value,-1L);
    }

    /**
     * 保存数据，并指定数据保质期
     * @param key
     * @param value
     * @param duration 数据的保质期时长
     */
    public void saveCache(String key,Object value,long duration){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String valueStr = mGson.toJson(value);
        if (duration != -1L) {
            duration += System.currentTimeMillis();
        }
        /*保存的数据附带时间*/
        CacheWithDuration cacheWithDuration = new CacheWithDuration(duration,valueStr);
        String cacheWithTime = mGson.toJson(cacheWithDuration);

        editor.putString(key,cacheWithTime);

        editor.apply();
    }

    public void delCache(String key){
        mSharedPreferences.edit().remove(key).apply();
    }

    public <T> T getCacheValue(String key,Class<T> clazz){
        String valueWithTime = mSharedPreferences.getString(key,null);
        if (valueWithTime == null) {
            return null;
        }

        CacheWithDuration cacheWithDuration = mGson.fromJson(valueWithTime,CacheWithDuration.class);
        long duration = cacheWithDuration.getDuration();
        if(duration != -1 && duration - System.currentTimeMillis() <= 0) {
            //数据过了保质期
            return null;
        } else {
            //数据还在有效期内
            String cacheValue = cacheWithDuration.getCache();
            T result = mGson.fromJson(cacheValue,clazz);
            return result;
        }
    }


}
