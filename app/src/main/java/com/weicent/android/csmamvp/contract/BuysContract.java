package com.weicent.android.csmamvp.contract;

import com.weicent.android.csmamvp.BasePresenter;
import com.weicent.android.csmamvp.BaseView;
import com.weicent.android.csmamvp.data.result.list.ResBuysList1;

/**
 * Created by admin on 2017/3/14.
 * 求购，presenter与view契合
 */
public interface BuysContract {

    //add
    interface AddView extends BaseView {
        void onSuccess();
    }

    interface AddPresenter extends BasePresenter {
        void httpAdd(String[] keys, String[] values, int aType);
    }

    //list
    interface View extends BaseView {
        //下拉刷新
        void onRefreshSuccess(ResBuysList1 resultJson);

        //上拉加载
        void onLoadSuccess(ResBuysList1 resultJson);

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
        void getList(String[] keys, String[] values, int aType, boolean isRefresh);
    }
}
