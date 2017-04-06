package com.weicent.android.csmamvp;

import com.ab.fragment.AbFragment;
import com.weicent.android.csmamvp.data.NetWorkWeb;

/**
 * Created by hzl520 on 2017/4/6.
 */
public class BaseFragment extends AbFragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (NetWorkWeb.getInstance().getRequestClient() == null) {
            return;
        } else {
            NetWorkWeb.getInstance().getRequestClient().cancelAllRequests(true);//关闭所有请求
        }
    }
}
