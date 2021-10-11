package com.cvte.taobaounion.model.domain;

/**
 * Created by user on 2020/11/4.
 */

public class TicketParams {
    private String title;
    private String url;

    public TicketParams(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
