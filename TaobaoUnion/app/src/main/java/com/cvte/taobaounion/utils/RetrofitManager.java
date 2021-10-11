package com.cvte.taobaounion.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by user on 2020/10/22.
 */

public class RetrofitManager {

    public static final RetrofitManager sRetrofitManager = new RetrofitManager();
    private final Retrofit mRetrofit;

    public static RetrofitManager getInstance(){
        return sRetrofitManager;
    }

    private RetrofitManager(){
        //创建Retrofit 单例
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

}
