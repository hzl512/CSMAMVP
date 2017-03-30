package com.weicent.android.csmamvp.ui.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.services.DownloadUpdateService;
import com.weicent.android.csmamvp.util.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * App更新弹出页面
 */
public class UpdateDialogActivity extends Activity {

    @BindView(R.id.textContent)
    TextView textContent;

    private static final String TAG = "DownloadUpdateService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);
        ButterKnife.bind(this);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= 14) {//当系统版本大于14时
            setFinishOnTouchOutside(false);//设置点击区域外不消失
        } else {
            Log.d(TAG, "系统版本号" + Build.VERSION.SDK_INT);
        }
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null ) { //重新打开的intent有值
            setIntent(intent);//重新赋值
            initData();
        }
    }

    private void initData() {
        textContent.setText(String.valueOf(SPUtil.get(this,Constants.UPDATE_DESCRIPTION, "有新版本可更新!")));
    }

    @OnClick({R.id.textCancel, R.id.textOk})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textCancel:
                finish();
                break;
            case R.id.textOk:
                if(android.os.Build.VERSION.SDK_INT>=9&& Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//当系统版本大于9时,并且有外部存储设备即sd卡时，并有读写权限，才提供下载管理方法
                    Log.d(TAG, "启动更新下载服务");
                    //启动更新下载服务
                    Intent intent1=new Intent(UpdateDialogActivity.this,DownloadUpdateService.class);
                    intent1.putExtra("type", 3);
                    startService(intent1);
                }else{//版本小于9，启动浏览器下载
                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(String.valueOf(SPUtil.get(this,Constants.UPDATE_URL, "")));
                    intent.setData(content_url);
                    startActivity(intent);
                }
                finish();
                break;
        }
    }

}
