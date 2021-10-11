package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.view.ITicketPagerCallback;

/**
 * Created by user on 2020/11/3.
 */

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {


    /**
     * 得到淘口令
     * @param title
     * @param url
     * @param cover
     */
    void getTicket(String title,String url,String cover);
}
