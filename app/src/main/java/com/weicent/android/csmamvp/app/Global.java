package com.weicent.android.csmamvp.app;

import com.weicent.android.csmamvp.R;

/**
 * Created by admin on 2017/3/10.
 * 保存项目中的缓存数据
 */
public class Global {

    private static Global sInstance=null;
    public static Global getInstance(){
        if (sInstance==null){
            sInstance= new Global();
        }
        return sInstance;
    }

    public int getCategoryView(int type){
        switch (type){
            case 1:
                return R.mipmap.icon_xydb;
            case 2:
                return R.mipmap.icon_sj;
            case 3:
                return R.mipmap.icon_dn;
            case 4:
                return R.mipmap.icon_smpj;
            case 5:
                return R.mipmap.icon_sm;
            case 6:
                return R.mipmap.icon_dq;
            case 7:
                return R.mipmap.icon_ydjs;
            case 8:
                return R.mipmap.icon_yfsm;
            case 9:
                return R.mipmap.icon_tsjc;
            case 10:
                return R.mipmap.icon_zl;
            case 11:
                return R.mipmap.icon_shyl;
            case 12:
                return R.mipmap.icon_qt;
        }
        return R.mipmap.icon_qt;
    }

}
