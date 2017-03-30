package com.weicent.android.csmamvp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.UsersContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Departments;
import com.weicent.android.csmamvp.data.model.result.Profession;
import com.weicent.android.csmamvp.presenter.UsersPresenter;
import com.weicent.android.csmamvp.ui.list.DepartmentsListActivity;
import com.weicent.android.csmamvp.ui.list.ProfessionListActivity;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 注册
 */
public class SignActivity extends AbActivity  implements UsersContract.SignView{

    @BindView(R.id.textDepartmentsID)
    TextView textDepartmentsID;
    @BindView(R.id.textProfessionID)
    TextView textProfessionID;
    @BindView(R.id.textUsersName)
    EditText textUsersName;
    @BindView(R.id.textUsersPwd)
    EditText textUsersPwd;
    @BindView(R.id.textUsersRePwd)
    EditText textUsersRePwd;
    @BindView(R.id.textUsersNickName)
    EditText textUsersNickName;
    @BindView(R.id.textUsersPhone)
    EditText textUsersPhone;
    @BindView(R.id.textUsersQQ)
    EditText textUsersQQ;
    @BindView(R.id.textUsersGrade)
    EditText textUsersGrade;

    private AbTitleBar mAbTitleBar = null;
    private Departments mDepartments = new Departments();
    private Profession mProfession = new Profession();
    private UsersPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("注册");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.tab_select_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
        mPresenter=new UsersPresenter(this,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                } else {
                    mDepartments = data.getParcelableExtra(Constants.MODEL_NAME);
                    textDepartmentsID.setText(mDepartments.departmentsName);
                }
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                } else {
                    mProfession = data.getParcelableExtra(Constants.MODEL_NAME);
                    textProfessionID.setText(mProfession.professionName);
                }
            }
        }
    }

    @OnClick({R.id.textDepartmentsID, R.id.textProfessionID, R.id.btnSure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textDepartmentsID://院系
                startActivityForResult(new Intent(SignActivity.this, DepartmentsListActivity.class), 0);
                break;
            case R.id.textProfessionID://专业
                if (MyStringUtil.isNullToToast(SignActivity.this, textDepartmentsID.getText().toString(), "院系"))
                    return;
                startActivityForResult(new Intent(SignActivity.this, ProfessionListActivity.class).putExtra(Constants.MODEL_NAME, mDepartments), 1);
                break;
            case R.id.btnSure://注册
                if (!textUsersPwd.getText().toString().equals(textUsersRePwd.getText().toString())) {
                    ToastUtil.showShort(SignActivity.this, "两次密码不一致");
                    return;
                }
                if (MyStringUtil.isNullToToast(SignActivity.this, textUsersName.getText().toString(), "用户账号")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersNickName.getText().toString(), "用户昵称")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersPhone.getText().toString(), "联系电话")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersPwd.getText().toString(), "用户密码")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersRePwd.getText().toString(), "密码确认")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersQQ.getText().toString(), "联系QQ")
                        || MyStringUtil.isNullToToast(SignActivity.this, textUsersGrade.getText().toString(), "年级")
                        || MyStringUtil.isNullToToast(SignActivity.this, textDepartmentsID.getText().toString(), "院系")
                        || MyStringUtil.isNullToToast(SignActivity.this, textProfessionID.getText().toString(), "专业")
                        ) return;

                mPresenter.sign(new String[]{
                        "usersName",
                        "usersPwd",
                        "usersNickName",
                        "usersPhone",
                        "usersQQ",
                        "departmentsID",
                        "professionID",
                        "usersGrade"
                },new String[]{
                        textUsersName.getText().toString(),
                        textUsersPwd.getText().toString(),
                        textUsersNickName.getText().toString(),
                        textUsersPhone.getText().toString(),
                        textUsersQQ.getText().toString(),
                        String.valueOf(mDepartments.id),
                        String.valueOf(mProfession.id),
                        textUsersGrade.getText().toString()
                },1);
                break;
        }
    }

    @Override
    public void onSignSuccess() {
        startActivity(new Intent(SignActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NetWorkWeb.getInstance().getRequestClient() == null) {
            return;
        } else {
            NetWorkWeb.getInstance().getRequestClient().cancelAllRequests(true);//关闭所有请求
        }
    }

}

