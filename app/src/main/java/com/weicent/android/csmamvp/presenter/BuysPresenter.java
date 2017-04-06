package com.weicent.android.csmamvp.presenter;

import android.content.Context;

import com.ab.fragment.AbDialogFragment;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.util.AbDialogUtil;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.BuysContract;
import com.weicent.android.csmamvp.data.BaseResult;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.result.list.ResBuysList1;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import org.apache.http.Header;

/**
 * Created by admin on 2017/3/14.
 * 求购Presenter
 */
public class BuysPresenter implements BuysContract.Presenter ,BuysContract.AddPresenter{

    private Context mContext;
    //list
    private BuysContract.View mView;
    //add
    private BuysContract.AddView mAddView;

    //list
    public BuysPresenter(BuysContract.View mView) {
        this.mView = mView;
    }
    //add
    public BuysPresenter(BuysContract.AddView mAddView, Context mContext) {
        this.mAddView = mAddView;
        this.mContext = mContext;
    }

    @Override
    public void start() {
        getList(new String[]{"paging","page_no","page_size"}
                ,new String[]{
                        String.valueOf(true),
                        String.valueOf(1),
                        String.valueOf(10)},6,true);
    }

    @Override
    public void getList(String[] keys, String[] values, int aType,final boolean isRefresh) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_BUYS_SERVLET, new ResultHandlerForJson<ResBuysList1>(ResBuysList1.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResBuysList1 resultJson) {
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
        }, MyStringUtil.toJsonString(keys,values), aType);
    }

    @Override
    public void httpAdd(final String[] keys, final String[] values, final int aType) {
        final AbLoadDialogFragment dialogFragment = AbDialogUtil.showLoadDialog(mContext, R.mipmap.ic_load, "操作中...");
        dialogFragment.setTextColor(mContext.getResources().getColor(R.color.black));
        dialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                NetWorkWeb.getInstance().doRequest(Constants.URL_BUYS_SERVLET, new ResultHandlerForJson<BaseResult>(BaseResult.class) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, BaseResult resultJson) {
                        if (resultJson.errorcode == 0) {
                            dialogFragment.loadFinish();
                            ToastUtil.showShort(mContext, "添加成功！");
                            mAddView.onSuccess();
                        } else {
                            ToastUtil.showShort(mContext, resultJson.errormsg);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                        ToastUtil.showShort(mContext, Constants.REQUEST_OUT_MSG);
                    }

                    @Override
                    public void onFinish() {
                        dialogFragment.loadFinish();
                    }
                }, MyStringUtil.toJsonString(keys,values), aType);
            }
        });
    }
}