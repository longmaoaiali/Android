package com.cvte.androidnetwork.domain;

import java.util.List;

/**
 * Created by user on 2020/8/25.
 */

public class GetTextItem {

    /**
     * success : true
     * code : 10000
     * message : 获取成功
     * data : [{"id":"1202177685208530944","title":"Android加载大图片，解决OOM问题","viewCount":104,"commentCount":50,"publishTime":"2019-12-04T10:47:50.192+0000","userName":"程序员拉大锯","cover":"/imgs/14.png"},{"id":"1202177685208530945","title":"Volley/Xutils对大图片处理算法源码分析","viewCount":269,"commentCount":24,"publishTime":"2019-12-04T10:47:50.192+0000","userName":"程序员拉大锯","cover":"/imgs/11.png"}]
     */

    private boolean success;
    private int code;
    private String message;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1202177685208530944
         * title : Android加载大图片，解决OOM问题
         * viewCount : 104
         * commentCount : 50
         * publishTime : 2019-12-04T10:47:50.192+0000
         * userName : 程序员拉大锯
         * cover : /imgs/14.png
         */

        private String id;
        private String title;
        private int viewCount;
        private int commentCount;
        private String publishTime;
        private String userName;
        private String cover;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getViewCount() {
            return viewCount;
        }

        public void setViewCount(int viewCount) {
            this.viewCount = viewCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
