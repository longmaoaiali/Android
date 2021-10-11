package com.cvte.taobaounion.view;

import com.cvte.taobaounion.base.IBaseCallback;
import com.cvte.taobaounion.model.domain.Categories;

/**
 * Created by user on 2020/10/22.
 */

public interface IHomeCallback extends IBaseCallback{

    void onCategoriesloaded(Categories categories);


}
