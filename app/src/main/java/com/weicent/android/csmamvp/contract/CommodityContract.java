package com.weicent.android.csmamvp.contract;

import com.loopj.android.http.RequestParams;
import com.weicent.android.csmamvp.BasePresenter;
import com.weicent.android.csmamvp.BaseView;
import com.weicent.android.csmamvp.data.BaseResult;
import com.weicent.android.csmamvp.data.result.list.ResCommodityList;
import com.weicent.android.csmamvp.data.result.model.ResCommodity;

/**
 * Created by admin on 2017/3/10.
 * 商品，presenter与view契合
 */
public interface CommodityContract {

    //add
    interface AddView extends BaseView {
        void addSuccess();
    }

    interface AddPresenter extends BasePresenter {
        void httpAdd(RequestParams requestParams);
    }

    //Detail
    interface DetailView extends BaseView {
        void httpGetDetailOnSuccess(ResCommodity resultJson);

        void httpGetDetailOnFailure();

        void httpGetDetailOnFinish();

        void httpUpdateViewsSuccess(BaseResult resultJson, int views);
    }

    interface DetailPresenter extends BasePresenter {
        void httpGetDetail(Integer id, int aType);

        void httpUpdateViews(Integer id, int views, int aType);
    }

    //List
    interface View extends BaseView {
        //下拉刷新
        void onRefreshSuccess(ResCommodityList resultJson);

        //上拉加载
        void onLoadSuccess(ResCommodityList resultJson);

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
        void getList(String request, int aType, boolean isRefresh);
    }

}