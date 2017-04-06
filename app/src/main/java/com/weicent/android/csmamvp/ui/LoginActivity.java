package com.weicent.android.csmamvp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.weicent.android.csmamvp.BaseActivity;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.contract.UsersContract;
import com.weicent.android.csmamvp.presenter.UsersPresenter;
import com.weicent.android.csmamvp.util.StringUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements UsersContract.LoginView {

    @BindView(R.id.textName)
    EditText textName;
    @BindView(R.id.textPwd)
    EditText textPwd;
    private long mExitTime = 0;
    private UsersPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter = new UsersPresenter(this, this);
    }

    //操作事件
    @OnClick({R.id.btnSign, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSign://注册
                mPresenter.sign();
                break;
            case R.id.btnLogin://登录
                if (StringUtil.isEmpty(textName.getText().toString())) {
                    ToastUtil.showShort(this, "用户名不能为空哦");
                } else if (StringUtil.isEmpty(textPwd.getText().toString())) {
                    ToastUtil.showShort(this, "密码不能为空哦");
                } else if (StringUtil.isEmpty(textName.getText().toString()) && StringUtil.isEmpty(textPwd.getText().toString())) {
                    ToastUtil.showShort(this, "用户名与密码不能为空哦");
                } else {
                    mPresenter.login(new String[]{
                            "usersName",
                            "usersPwd"}
                            ,new String[]{
                                    textName.getText().toString().trim(),
                                    textPwd.getText().toString().trim()}, 5);
                }
                break;
        }
    }

    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
                ToastUtil.showLong(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

