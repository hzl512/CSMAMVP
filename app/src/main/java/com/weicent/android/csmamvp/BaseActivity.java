package com.weicent.android.csmamvp;

import com.ab.activity.AbActivity;
import com.ab.util.AbLogUtil;
import com.weicent.android.csmamvp.data.NetWorkWeb;

/**
 * Created by hzl520 on 2017/4/6.
 */
public class BaseActivity extends AbActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NetWorkWeb.getInstance().getRequestClient() == null) {
            return;
        } else {
            NetWorkWeb.getInstance().getRequestClient().cancelAllRequests(true);//关闭所有请求
            AbLogUtil.d("BaseActivity","cancelAllRequests");
        }
    }
}
