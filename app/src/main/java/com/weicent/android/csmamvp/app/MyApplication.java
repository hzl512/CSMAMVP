package com.weicent.android.csmamvp.app;

import android.app.Application;

/**
 * Created by admin on 2017/3/9.
 */
public class MyApplication extends Application {

    private static MyApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static MyApplication getContext() {
        return mContext;
    }
}
