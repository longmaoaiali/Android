package com.cvte.taobaounion.view;

import com.cvte.taobaounion.base.IBaseCallback;
import com.cvte.taobaounion.model.domain.TicketResult;

/**
 * Created by user on 2020/11/3.
 */

public interface ITicketPagerCallback extends IBaseCallback {

    void onTicketLoaded(String cover, TicketResult result);

}
