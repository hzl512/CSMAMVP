package com.weicent.android.csmamvp.presenter;

import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.DepartmentsContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.result.list.ResDepartmentsList;
import com.weicent.android.csmamvp.util.MyStringUtil;

import org.apache.http.Header;

/**
 * Created by admin on 2017/3/15.
 * 院系，Presenter
 */
public class DepartmentsPresenter implements DepartmentsContract.Presenter {
    private DepartmentsContract.View mView;

    public DepartmentsPresenter(DepartmentsContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void start() {

    }

    @Override
    public void getList(boolean paging, int page, int size, int aType, final boolean isRefresh) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_DEPARTMENTS_SERVLET, new ResultHandlerForJson<ResDepartmentsList>(ResDepartmentsList.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResDepartmentsList resultJson) {
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
                        String.valueOf(size)}), aType);
    }

}
