package com.weicent.android.csmamvp.contract;

import com.weicent.android.csmamvp.BasePresenter;
import com.weicent.android.csmamvp.BaseView;
import com.weicent.android.csmamvp.data.result.list.ResCategoryList;

/**
 * Created by admin on 2017/3/10.
 * 分类，presenter与view契合
 */
public interface CategoryContract {
    //list
    interface View extends BaseView {
        //下拉刷新
        void onRefreshSuccess(ResCategoryList resultJson);

        //上拉加载
        void onLoadSuccess(ResCategoryList resultJson);

        //下拉错误处理
        void onRefreshFailure();

        //下拉
        void onRefreshFinish();

        //上拉错误处理
        void onLoadFailure();

        //上拉
        void onLoadFinish();
    }

    interface Presenter extends BasePresenter {
        void getList(boolean paging, int page, int size, int aType, boolean isRefresh);
    }
}
