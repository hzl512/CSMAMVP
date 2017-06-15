package com.weicent.android.csmamvp.presenter;

import android.content.Context;

import com.ab.fragment.AbDialogFragment;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbLogUtil;
import com.loopj.android.http.RequestParams;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CommodityContract;
import com.weicent.android.csmamvp.data.BaseResult;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.result.list.ResCommodityList;
import com.weicent.android.csmamvp.data.result.model.ResCommodity;
import com.weicent.android.csmamvp.util.ACacheUtil;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import org.apache.http.Header;

/**
 * Created by admin on 2017/3/13.
 * 商品Presenter
 */
public class CommodityPresenter implements CommodityContract.Presenter ,CommodityContract.DetailPresenter,CommodityContract.AddPresenter{

//    private static final String TAG = "CommodityPresenter";
    private Context mContext;
    private CommodityContract.View mView;
    private CommodityContract.DetailView mDetailView;
    private CommodityContract.AddView mAddView;
    private ACacheUtil mACacheUtil;

    //list
    public CommodityPresenter(CommodityContract.View mView,Context context) {
        this.mView = mView;
        this.mContext=context;
        this.mACacheUtil=ACacheUtil.get(mContext);
    }
    //detail
    public CommodityPresenter(CommodityContract.DetailView mDetailView,Context context) {
        this.mDetailView = mDetailView;
        this.mContext=context;
        this.mACacheUtil=ACacheUtil.get(mContext);
    }
    //add
    public CommodityPresenter(CommodityContract.AddView mAddView,Context context) {
        this.mAddView = mAddView;
        this.mContext=context;
        this.mACacheUtil=ACacheUtil.get(mContext);
    }

    @Override
    public void start() {
//        getList(true,1,10,4,true);
    }

    @Override
    public void getList(boolean paging, int page, int size, int aType, final boolean isRefresh) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_COMMODITY_SERVLET, new ResultHandlerForJson<ResCommodityList>(ResCommodityList.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResCommodityList resultJson) {
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

    @Override
    public void getList(String request, int aType, final boolean isRefresh) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_COMMODITY_SERVLET, new ResultHandlerForJson<ResCommodityList>(ResCommodityList.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResCommodityList resultJson) {
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
        }, request, aType);
    }

    @Override
    public void httpGetDetail(final Integer id, int aType) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_COMMODITY_SERVLET, new ResultHandlerForJson<ResCommodity>(ResCommodity.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResCommodity resultJson) {
                mDetailView.httpGetDetailOnSuccess(resultJson);
                mACacheUtil.put(Constants.URL_COMMODITY_SERVLET+id,resultJson);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                mDetailView.httpGetDetailOnFailure();
            }

            @Override
            public void onFinish() {
                mDetailView.httpGetDetailOnFinish();
            }
        }, MyStringUtil.toJsonString(new String[]{"id"},new String[]{String.valueOf(id)}), aType);
    }

    @Override
    public void httpUpdateViews(Integer id, final int views,int aType) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_COMMODITY_SERVLET, new ResultHandlerForJson<BaseResult>(BaseResult.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, BaseResult resultJson) {
                mDetailView.httpUpdateViewsSuccess(resultJson,views);
            }
        }, MyStringUtil.toJsonString(new String[]{"id"},new String[]{String.valueOf(id)}), aType);
    }

    @Override
    public void getCacheDetail(final Integer id, int aType) {
        ResCommodity resCommodity=(ResCommodity)mACacheUtil.getAsObjectOfMap(Constants.URL_COMMODITY_SERVLET+id);
        if (resCommodity==null){
            httpGetDetail(id,aType);
            AbLogUtil.d("CommodityPresenter","1");
        }else {
            mDetailView.httpGetDetailOnFinish();
            mDetailView.httpGetDetailOnSuccess(resCommodity);
            AbLogUtil.d("CommodityPresenter",resCommodity.data.toString());
        }
    }

    @Override
    public void httpAdd(final RequestParams requestParams) {
        final AbLoadDialogFragment dialogFragment = AbDialogUtil.showLoadDialog(mContext, R.mipmap.ic_load, "操作中...");
        dialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                NetWorkWeb.getInstance().doRequest(Constants.URL_COMMODITY_ADD_SERVLET, new ResultHandlerForJson<BaseResult>(BaseResult.class) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, BaseResult resultJson) {
                        if (resultJson.errorcode == 0) {
                            dialogFragment.loadFinish();
                            ToastUtil.showShort(mContext, "添加成功！");
                            mAddView.addSuccess();
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
                }, requestParams);
            }
        });
    }
}