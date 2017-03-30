package com.weicent.android.csmamvp.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Users;
import com.weicent.android.csmamvp.ui.LoginActivity;
import com.weicent.android.csmamvp.ui.detail.UsersDetailActivity;
import com.weicent.android.csmamvp.ui.list.BuysListActivity;
import com.weicent.android.csmamvp.ui.list.MyCommodityListActivity;
import com.weicent.android.csmamvp.ui.other.CheckUpdateActivity;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我
 */
public class UsersFragment extends android.app.Fragment {

    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.textRight)
    TextView textRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        if (((int) SPUtil.get(getActivity(), Constants.SHARED_ID, 0)) <= 0) {
            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    private void initView() {
        textRight.setVisibility(View.GONE);
        textTitle.setText("我");
    }

    //操作事件
    @OnClick({R.id.btnPersonalData, R.id.btnAbout, R.id.btnLoginOut, R.id.btnCommodity, R.id.btnBuys,R.id.btnCheckUpdate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPersonalData://个人资料
                startActivity(new Intent(getActivity(), UsersDetailActivity.class));
                break;
            case R.id.btnAbout://关于
//                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.btnCommodity://我的商品
                startActivity(new Intent(getActivity(), MyCommodityListActivity.class).putExtra("type", 1));
                break;
            case R.id.btnBuys://我的求购
                startActivity(new Intent(getActivity(), BuysListActivity.class));
                break;
            case R.id.btnCheckUpdate://版本更新
                startActivity(new Intent(getActivity(), CheckUpdateActivity.class));
                break;
            case R.id.btnLoginOut://注销
                AbDialogUtil.showAlertDialog(getActivity(), "提示", "确定要注销当前账号吗？"
                        , new AbAlertDialogFragment.AbDialogOnClickListener() {
                            @Override
                            public void onPositiveClick() {
                                new Users().setClearAll();
                                ToastUtil.showShort(getActivity(), "注销成功！");
                                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                getActivity().finish();
                            }

                            @Override
                            public void onNegativeClick() {
                            }
                        });
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (NetWorkWeb.getInstance().getRequestClient() == null) {
            return;
        } else {
            NetWorkWeb.getInstance().getRequestClient().cancelAllRequests(true);//关闭所有请求
        }
    }
}