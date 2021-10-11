package com.cvte.taobaounion.view;

import com.cvte.taobaounion.base.IBaseCallback;
import com.cvte.taobaounion.model.domain.SelectedContentNew;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;

/**
 * Created by user on 2020/11/5.
 */

public interface ISelectedPageCallback extends IBaseCallback {

    /**
     * 获取分类内容的回调
     * @param categories
     */
    void onCategoriesLoaded(SelectedPageCategory categories);

    /**
     * 根据分类ID获取的具体内容
     * @param content
     */
    void onContentLoaded(SelectedContentNew content);
}
