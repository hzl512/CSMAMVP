package com.weicent.android.csmamvp.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.ab.activity.AbActivity;
import com.ab.util.AbLogUtil;
import com.ab.view.sliding.AbBottomTabView;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.services.DownloadUpdateService;
import com.weicent.android.csmamvp.ui.fragment.BuysFragment;
import com.weicent.android.csmamvp.ui.fragment.CategoryFragment;
import com.weicent.android.csmamvp.ui.fragment.CommodityFragment;
import com.weicent.android.csmamvp.ui.fragment.UsersFragment;
import com.weicent.android.csmamvp.util.ACacheUtil;
import com.weicent.android.csmamvp.util.AppUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页
 */
public class MainActivity extends AbActivity {

    @BindView(R.id.abBottomTabView)
    AbBottomTabView abBottomTabView;
    private List<Drawable> mTabDrawables = null;
    private long mExitTime = 0;
    private ACacheUtil mACacheUntil=ACacheUtil.get(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("智能ABC","智能ABC");

        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {
                if (!AppUtil.isServiceWork(MainActivity.this, Constants.DOWNLOAD_UPDATE_SERVICE_BAG_NAME)) {
                    //启动更新服务
                    Intent intent1=new Intent(MainActivity.this,DownloadUpdateService.class);
                    intent1.putExtra("type", 1);
                    startService(intent1);
                }
            }
        },R.string.permission_tips, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CommodityFragment());
        fragments.add(new CategoryFragment());
        fragments.add(new BuysFragment());
        fragments.add(new UsersFragment());

        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("首页");
        tabTexts.add("分类");
        tabTexts.add("求购");
        tabTexts.add("我");

        //缓存数量
        abBottomTabView.getViewPager().setOffscreenPageLimit(3);
        //设置样式
        abBottomTabView.setTabTextColor(R.color.tab_text_color);
        abBottomTabView.setTabSelectColor(R.color.tab_select_color);
        abBottomTabView.setTabBackgroundResource(R.mipmap.tablayout_bg2);
        abBottomTabView.setTabLayoutBackgroundResource(R.mipmap.tablayout_bg2);
        //注意图片的顺序
        mTabDrawables = new ArrayList<>();

        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_home_normal));
        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_home_checked));

        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_classify_normal));
        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_classify_checked));

        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_find_normal));
        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_find_checked));

        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_profilo_normal));
        mTabDrawables.add(this.getResources().getDrawable(R.mipmap.tab_profilo_checked));

        abBottomTabView.setTabCompoundDrawablesBounds(0, 0, 40, 40);
        //增加一组
        abBottomTabView.addItemViews(tabTexts, fragments, mTabDrawables);
        abBottomTabView.setTabPadding(10, 10, 10, 10);
        AbLogUtil.d("MainActivity","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        AbLogUtil.d("MainActivity","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AbLogUtil.d("MainActivity","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AbLogUtil.d("MainActivity","onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AbLogUtil.d("MainActivity","onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AbLogUtil.d("MainActivity","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AbLogUtil.d("MainActivity","onDestroy");
    }

    /**
     * 按键点击事件处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showLong(MainActivity.this,"再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                mACacheUntil.clear();
                this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
