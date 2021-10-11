package com.cvte.taobaounion.model;

import com.cvte.taobaounion.model.domain.Categories;
import com.cvte.taobaounion.model.domain.HomePagerContent;
import com.cvte.taobaounion.model.domain.SearchRecommand;
import com.cvte.taobaounion.model.domain.SearchResult;
import com.cvte.taobaounion.model.domain.SelectedContentNew;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;
import com.cvte.taobaounion.model.domain.SellContent;
import com.cvte.taobaounion.model.domain.TicketParams;
import com.cvte.taobaounion.model.domain.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by user on 2020/10/22.
 */

public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<HomePagerContent> getHomePagerContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET("recommend/categories")
    Call<SelectedPageCategory> getSelectedPageCategories();

    @GET()
    Call<SelectedContentNew> getSelectedPageContent(@Url String url);

    @GET
    Call<SellContent> getOnSellPageContent(@Url String url);

    @GET("search/recommend")
    Call<SearchRecommand> getRecommendWorlds();

    @GET("search")
    Call<SearchResult> doSearch(@Query("page") int page,@Query("keyword") String keyword);

}
