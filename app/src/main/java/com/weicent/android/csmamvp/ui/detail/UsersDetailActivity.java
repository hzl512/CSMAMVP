package com.weicent.android.csmamvp.ui.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.IBinDing;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.UsersContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Users1;
import com.weicent.android.csmamvp.data.result.model.ResUsers1;
import com.weicent.android.csmamvp.presenter.UsersPresenter;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 个人资料
 */
public class UsersDetailActivity extends AbActivity implements IBinDing ,UsersContract.DetailView{

    @BindView(R.id.layoutLoading)
    RelativeLayout layoutLoading;
    @BindView(R.id.layoutMsg)
    RelativeLayout layoutMsg;
    @BindView(R.id.layoutContext)
    LinearLayout layoutContext;

    @BindView(R.id.textUsersName)
    TextView textUsersName;
    @BindView(R.id.textUsersAddTime)
    TextView textUsersAddTime;
    @BindView(R.id.textUsersNickName)
    TextView textUsersNickName;
    @BindView(R.id.textUsersPhone)
    TextView textUsersPhone;
    @BindView(R.id.textUsersQQ)
    TextView textUsersQQ;
    @BindView(R.id.textDepartmentsID)
    TextView textDepartmentsID;
    @BindView(R.id.textProfessionID)
    TextView textProfessionID;
    @BindView(R.id.textUsersGrade)
    TextView textUsersGrade;

    private AbTitleBar mAbTitleBar = null;
    private UsersPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_users_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mPresenter=new UsersPresenter(this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("个人资料");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.title_bar_back_ground_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
        layoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        initData();
    }

    @Override
    public void initData() {
        layoutContext.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMsg.setVisibility(View.GONE);
        mPresenter.httpGetUsersDetail(new String[]{"usersName","usersPwd"},new String[]{
                SPUtil.get(this,Constants.SHARED_USERNAME,"").toString(),
                SPUtil.get(this,Constants.SHARED_PWD,"").toString()
        },6);
    }

    @Override
    public void onSuccess(ResUsers1 resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultModelString("UsersServlet first", resultJson.data);
            setValue(resultJson.data);
        } else {
            layoutMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure() {
        ToastUtil.showShort(UsersDetailActivity.this, Constants.NET_OUT_MSG);
        layoutMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinish() {
        layoutLoading.setVisibility(View.GONE);
    }

    private void setValue(Users1 model) {
        textUsersName.setText(model.usersName);
        textUsersAddTime.setText(model.usersAddTime);
        textUsersNickName.setText(model.usersNickName);
        textUsersPhone.setText(model.usersPhone);
        textUsersQQ.setText(model.usersQQ);
        textDepartmentsID.setText(model.departmentsID);
        textProfessionID.setText(model.professionID);
        textUsersGrade.setText(model.usersGrade);
        layoutContext.setVisibility(View.VISIBLE);
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