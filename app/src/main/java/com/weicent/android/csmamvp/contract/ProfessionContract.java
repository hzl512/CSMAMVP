package com.weicent.android.csmamvp.contract;

import com.weicent.android.csmamvp.BasePresenter;
import com.weicent.android.csmamvp.BaseView;
import com.weicent.android.csmamvp.data.result.list.ResProfessionList;

/**
 * Created by admin on 2017/3/15.
 * 专业，presenter与view契合
 */
public interface ProfessionContract {
    interface View extends BaseView {
        //下拉刷新
        void onRefreshSuccess(ResProfessionList resultJson);

        //上拉加载
        void onLoadSuccess(ResProfessionList resultJson);

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
        void getList(boolean paging, int page, int size, Integer departmentsID, int aType, boolean isRefresh);
    }
}
