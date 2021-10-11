package com.cvte.ximalaya.presenters;

import android.support.annotation.Nullable;

import com.cvte.ximalaya.api.XimalayaDataApi;
import com.cvte.ximalaya.interfaces.IRecommendPresenter;
import com.cvte.ximalaya.interfaces.IRecommendViewCallback;
import com.cvte.ximalaya.utils.Constants;
import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2020/9/4.
 */

/*逻辑层来实现接口 UI动作调用逻辑层 逻辑层回调实现UI更新*/
public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";

    /*callback list*/
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    /*懒汉式单例*/
    private RecommendPresenter(){}

    private static RecommendPresenter sInstance = null;

    public static RecommendPresenter getInstance(){
        if(sInstance==null)
        {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    @Override
    public void getRecommendList() {
        getRecommendData();
    }

    private void getRecommendData() {
        LogUtil.d(TAG,"request Recommend Data");
        updateLoadingPicture();

        XimalayaDataApi ximalayaDataApi = XimalayaDataApi.getXimalayaDataApi();
        ximalayaDataApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name --> "+Thread.currentThread().getName());
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        LogUtil.d(TAG,"size -->" + albumList.size());
                        //得到数据以后，更新UI
                        //updateRecommendUI(albumList);
                        handlerRecommendResult(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"errorCode --> "+i+" errorMsg -->"+s );
                /*处理网络错误*/
                handlerError(i,s);
            }
        });
        //封装map参数，实现回调函数
//        Map<String, String> map = new HashMap<String, String>();
//        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMAND_COUNT+"");
//        CommonRequest.getGuessLikeAlbum(map, );
    }

    private void handlerError(int i, String s) {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        //便利所有的注册对象，通知UI更新
        if (albumList != null) {
            if (albumList.size()==0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    //处理数据为空
                    callback.onEmpty();
                }
            }else{
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }

        }
    }

    private void updateLoadingPicture(){
        for (IRecommendViewCallback callback : mCallbacks) {
            /*先在页面上加载一个loading 图片*/
            callback.onLoading();
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}


