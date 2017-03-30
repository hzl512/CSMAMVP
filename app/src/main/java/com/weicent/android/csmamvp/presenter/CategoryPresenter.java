package com.weicent.android.csmamvp.presenter;

import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CategoryContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.result.list.ResCategoryList;
import com.weicent.android.csmamvp.util.MyStringUtil;

import org.apache.http.Header;

/**
 * Created by admin on 2017/3/13.
 * 商品Presenter
 */
public class CategoryPresenter implements CategoryContract.Presenter {

//    private static final String TAG = "CategoryPresenter";
    private CategoryContract.View mView;

    public CategoryPresenter(CategoryContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void start() {
        getList(true,1,10,4,true);
    }

    @Override
    public void getList(boolean paging, int page, int size, int aType, final boolean isRefresh) {
//        LogUtil.v(TAG,"getCategoryList");
        NetWorkWeb.getInstance().doRequest(Constants.URL_CATEGORY_SERVLET, new ResultHandlerForJson<ResCategoryList>(ResCategoryList.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResCategoryList resultJson) {
                if (isRefresh){
                    mView.onRefreshSuccess(resultJson);
                }else {
                    mView.onLoadSuccess(resultJson);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                if (isRefresh){
                    mView.onRefreshFailure();
                }else {
                    mView.onLoadFailure();
                }
            }

            @Override
            public void onFinish() {
                if (isRefresh){
                    mView.onRefreshFinish();
                }else {
                    mView.onLoadFinish();
                }
            }
        }, MyStringUtil.toJsonString(new String[]{"paging","page_no","page_size"}
                ,new String[]{
                        String.valueOf(paging),
                        String.valueOf(page),
                        String.valueOf(size),}), aType);
    }

}
