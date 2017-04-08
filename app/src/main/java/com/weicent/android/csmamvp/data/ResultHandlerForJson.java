package com.weicent.android.csmamvp.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.weicent.android.csmamvp.app.MyApplication;
import com.weicent.android.csmamvp.util.ToastUtil;

import org.apache.http.Header;

//使用gson将json请求结果对象化
public abstract class ResultHandlerForJson<T extends Object> extends TextHttpResponseHandler {
    private Class<T> clazz;
    private Gson gson;

    protected ResultHandlerForJson(Class<T> clazz) {
        this.clazz = clazz;
        gson = new Gson();
    }

    /**
     * 使用Gson解析resultString字符串数据
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, String resultString) {
        try {
            if (TextUtils.isEmpty(resultString)){
                ToastUtil.showLong(MyApplication.getContext(),"服务器返回为空！");
                return;
            }
            T t = gson.fromJson(resultString, clazz);
            if (statusCode != 200) {
                return;
            }
            try {
                onSuccess(statusCode, headers, t);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showLong(MyApplication.getContext(),"服务器返回错误！");
            }
        } catch (TypeNotPresentException e) {
            e.printStackTrace();
            ToastUtil.showLong(MyApplication.getContext(),"数据解析错误");
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers,
                          String resultString, Throwable throwable) {
    }

    /**
     * 使用Gson获取Json对象
     *
     * @param statusCode 状态代码
     * @param headers    http头信息
     * @param resultJson Json转换之后的对象
     */
    public abstract void onSuccess(int statusCode, Header[] headers,
                                   T resultJson);

}
