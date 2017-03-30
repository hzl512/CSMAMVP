package com.weicent.android.csmamvp.presenter;

import android.content.Context;
import android.content.Intent;

import com.ab.fragment.AbDialogFragment;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbStrUtil;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.UsersContract;
import com.weicent.android.csmamvp.data.BaseResult;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.ResultHandlerForJson;
import com.weicent.android.csmamvp.data.model.result.Users;
import com.weicent.android.csmamvp.data.result.model.ResUsers;
import com.weicent.android.csmamvp.data.result.model.ResUsers1;
import com.weicent.android.csmamvp.ui.SignActivity;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import org.apache.http.Header;

/**
 * Created by admin on 2017/3/16.
 * 用户业务呈现者
 */
public class UsersPresenter implements UsersContract.LoginPresenter,UsersContract.SignPresenter,UsersContract.DetailPresenter {

    //detail
    private UsersContract.DetailView mDetailView;
    //login
    private UsersContract.LoginView mLoginView;
    //sign
    private UsersContract.SignView mSignView;
    private Context mContext;

    public UsersPresenter(UsersContract.SignView mSignView, Context mContext) {
        this.mSignView = mSignView;
        this.mContext = mContext;
    }

    public UsersPresenter(UsersContract.LoginView mLoginView, Context mContext) {
        this.mLoginView = mLoginView;
        this.mContext = mContext;
    }

    public UsersPresenter(UsersContract.DetailView mDetailView) {
        this.mDetailView = mDetailView;
    }

    @Override
    public void start() {

    }

    @Override
    public void sign() {
        mContext.startActivity(new Intent(mContext, SignActivity.class));
    }

    @Override
    public void login(final String[] keys, final String[] values, int aType) {
        final AbLoadDialogFragment dialogFragment = AbDialogUtil.showLoadDialog(mContext, R.mipmap.ic_load, "正在登录,请稍候...");
        dialogFragment.setTextColor(R.color.black);
        dialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                NetWorkWeb.getInstance().doRequest(Constants.URL_USERS_SERVLET, new ResultHandlerForJson<ResUsers>(ResUsers.class) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, ResUsers resultJson) {
                        dialogFragment.loadFinish();
                        if (resultJson.errorcode == 0) {
                            NetWorkWeb.getInstance().doLogResultModelString("MineServlet first", resultJson.data);
                            if (resultJson.data != null && !AbStrUtil.isEmpty(resultJson.data.usersName)) {
                                Users model = new Users();
                                model.setAll(resultJson.data.usersName, resultJson.data.usersPwd, resultJson.data.id);
                                if (model != null) {
                                    mLoginView.onLoginSuccess();
                                }
                            } else {
                                ToastUtil.showShort(mContext, "登录失败，请重试");
                            }

                        } else {
                            ToastUtil.showShort(mContext, resultJson.errormsg);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                        ToastUtil.showShort(mContext, "登录失败，请重试");
                    }

                    @Override
                    public void onFinish() {
                        dialogFragment.loadFinish();
                    }
                }, MyStringUtil.toJsonString(keys
                        ,values), 5);
            }
        });
    }

    @Override
    public void sign(final String[] keys, final String[] values, final int aType) {
        final AbLoadDialogFragment mDialogFragment = AbDialogUtil.showLoadDialog(mContext, R.mipmap.ic_load, "请稍候...");
        mDialogFragment.setTextColor(R.color.black);
        mDialogFragment.setAbDialogOnLoadListener(new AbDialogFragment.AbDialogOnLoadListener() {
            @Override
            public void onLoad() {
                NetWorkWeb.getInstance().doRequest(Constants.URL_USERS_SERVLET, new ResultHandlerForJson<BaseResult>(BaseResult.class) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, BaseResult resultJson) {
                        if (resultJson.errorcode == 0) {
                            mDialogFragment.loadFinish();
                            ToastUtil.showShort(mContext, "注册成功！");
                            mSignView.onSignSuccess();
                        } else {
                            ToastUtil.showShort(mContext, resultJson.errormsg);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                        ToastUtil.showShort(mContext, Constants.NET_OUT_MSG);
                    }

                    @Override
                    public void onFinish() {
                        mDialogFragment.loadFinish();
                    }
                }, MyStringUtil.toJsonString(keys,values), aType);
            }
        });
    }

    @Override
    public void httpGetUsersDetail(String[] keys, String[] values, int aType) {
        NetWorkWeb.getInstance().doRequest(Constants.URL_USERS_SERVLET, new ResultHandlerForJson<ResUsers1>(ResUsers1.class) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, ResUsers1 resultJson) {
                mDetailView.onSuccess(resultJson);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String resultString, Throwable throwable) {
                mDetailView.onFailure();
            }

            @Override
            public void onFinish() {
                mDetailView.onFinish();
            }
        }, MyStringUtil.toJsonString(keys,values), aType);
    }
}