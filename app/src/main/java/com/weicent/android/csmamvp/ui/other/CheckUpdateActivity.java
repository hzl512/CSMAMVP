package com.weicent.android.csmamvp.ui.other;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.services.DownloadUpdateService;
import com.weicent.android.csmamvp.util.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 检查更新
 */
public class CheckUpdateActivity extends AbActivity {

    @BindView(R.id.textCheckUpdateVersion)
    TextView textCheckUpdateVersion;

    private ProgressDialog mPgd=null;
    private AbTitleBar mAbTitleBar = null;

    private DownloadUpdateService mMainService;//获取服务
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_check_update);
        ButterKnife.bind(this);
        mAbTitleBar=this.getTitleBar();
        mAbTitleBar.setTitleText("版本更新");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.tab_select_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
        textCheckUpdateVersion.setText("v" + getVerName());
        mHandler=new Handler();
        mPgd=new ProgressDialog(this);
        mPgd.setTitle("处理中...");
        mPgd.setMessage("请等待...");
        mPgd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if ((keyCode == KeyEvent.KEYCODE_BACK) && (mPgd.isShowing())) {
//					pgd.dismiss();// 这里写明模拟menu的PopupWindow退出就行
//	                return true;  //返回false 就调用系统的返回,
                }
                return false;
            }
        });
        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {
                if (!AppUtil.isServiceWork(CheckUpdateActivity.this, Constants.DOWNLOAD_UPDATE_SERVICE_BAG_NAME)){
                    Intent intent1=new Intent(CheckUpdateActivity.this,DownloadUpdateService.class);
                    startService(intent1);
                }
                connection();
            }
        },R.string.permission_tips, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @OnClick(R.id.btnCheckUpdate)
    public void onClick() {
        Log.d("CheckUpdateActivity", "btn_checkUpdate");
        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {
                try{
                    if (!AppUtil.isServiceWork(CheckUpdateActivity.this,Constants.DOWNLOAD_UPDATE_SERVICE_BAG_NAME)){
                        Intent intent1=new Intent(CheckUpdateActivity.this,DownloadUpdateService.class);
                        startService(intent1);
                        connection();
                    }
                    if(!mPgd.isShowing()){
                        mPgd.show();
                    }
                    new Thread(){
                        public void run(){
                            if (mMainService!=null){
                                mMainService.Check();
                            }else {
                                mPgd.dismiss();
                            }
                        }
                    }.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },R.string.permission_tips, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void connection() {  //绑定服务
//        Intent intent = new Intent("xxx"); //隐式绑定 android5.0以上不支持
        Intent intent = new Intent(this,DownloadUpdateService.class); //显示绑定
        bindService(intent, sc, Context.BIND_AUTO_CREATE);          // bindService 不执行service的start方法
    }

    private ServiceConnection sc = new ServiceConnection() {  //服务绑定后，获得服务类
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {       //connect Service
            mMainService = ((DownloadUpdateService.MyBinder) (service)).getService();
            if (mMainService != null) {
                mMainService.setOnCheckUpdateListener(new DownloadUpdateService.ICallBack() {

                    @Override
                    public void onUpdate(final boolean b, final String msg) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mPgd.dismiss();
                                if(!b) Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mMainService=null;
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }

    public  String getVerName() {
        String verName = "";
        try {
            verName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("check_update", e.getMessage());
        }
        return verName;
    }



}
