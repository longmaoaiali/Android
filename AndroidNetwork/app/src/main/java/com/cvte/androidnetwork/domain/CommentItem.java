package com.cvte.androidnetwork.domain;

/**
 * Created by user on 2020/8/26.
 */

public class CommentItem {
    private int articleId;
    private String commentContent;

    public CommentItem(int articleId, String commentContent) {
        this.articleId = articleId;
        this.commentContent = commentContent;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
