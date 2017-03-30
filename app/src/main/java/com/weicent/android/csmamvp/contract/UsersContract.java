package com.weicent.android.csmamvp.contract;

import com.weicent.android.csmamvp.BasePresenter;
import com.weicent.android.csmamvp.BaseView;
import com.weicent.android.csmamvp.data.result.model.ResUsers1;

/**
 * Created by admin on 2017/3/16.
 * 用户业务契约者
 */
public interface UsersContract {

    //detail
    interface DetailView extends BaseView {
        void onSuccess(ResUsers1 resultJson);

        void onFailure();

        void onFinish();
    }

    interface DetailPresenter extends BasePresenter {
        void httpGetUsersDetail(String[] keys, String[] values, int aType);
    }

    //login
    interface LoginView extends BaseView {
        void onLoginSuccess();
    }

    interface LoginPresenter extends BasePresenter {
        void sign();

        void login(String[] keys, String[] values, int aType);
    }

    //sign
    interface SignView extends BaseView {
        void onSignSuccess();
    }

    interface SignPresenter extends BasePresenter {
        void sign(String[] keys, String[] values, int aType);
    }
}
