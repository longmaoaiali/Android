package com.cvte.taobaounion.utils;

/**
 * Created by user on 2020/10/26.
 */

public class UrlUtils {
    public static String createHomePagerUrl(int materialId, int page){
        return "discovery/"+materialId+"/"+page;
    }

    public static String getCoverPath(String pict_url,int size) {
        return "https:"+pict_url+"_"+size+"x"+size+".jpg";
    }

    public static String getCoverPath(String url) {
        if(url.startsWith("http")||url.startsWith("https")){
            return url;
        }else {
            return "https:"+url;
        }
    }

    public static String getTicketUrl(String url) {
        if(url.startsWith("http")||url.startsWith("https")){
            return url;
        }else {
            return "https:"+url;
        }
    }

    public static String getSelectedPageContentUrl(int categoryId) {
        return "recommend/"+categoryId;
    }

    public static String getOnSellPageUrl(int currentPage) {
        return "onSell/"+currentPage;
    }
}
