package com.weicent.android.csmamvp.ui.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.PermissionActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.fragment.AbFragment;
import com.ab.util.AbDialogUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.result.BuysAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.BuysContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.result.list.ResBuysList1;
import com.weicent.android.csmamvp.presenter.BuysPresenter;
import com.weicent.android.csmamvp.ui.LoginActivity;
import com.weicent.android.csmamvp.ui.add.BuysAddActivity;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.ButterKnife;


/**
 * 求购列表
 */
public class BuysFragment extends AbFragment implements AbPullToRefreshView.OnHeaderRefreshListener,BuysContract.View, AbPullToRefreshView.OnFooterLoadListener {

    ListView listView;
    TextView textTitle, textRight;
    AbPullToRefreshView abPullToRefreshView;

    private BuysPresenter mPresenter;
    private BuysAdapter mAdapter = null;//数据适配器
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 10;//页大小

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buys, null);
        abPullToRefreshView = ButterKnife.findById(view, R.id.abPullToRefreshView);
        listView = ButterKnife.findById(view, R.id.listView);
        textTitle = ButterKnife.findById(view, R.id.textTitle);
        textRight = ButterKnife.findById(view, R.id.textRight);
        initView();
        this.setAbFragmentOnLoadListener(new AbFragmentOnLoadListener() {

            @Override
            public void onLoad() {
                onHeaderRefresh(null);
            }

        });
        return view;
    }

    public void initView() {
        textTitle.setText("求购专区");
        textRight.setVisibility(View.VISIBLE);
        textRight.setText("发布求购");
        mPresenter=new BuysPresenter(this);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((int) SPUtil.get(getActivity(), Constants.SHARED_ID,0)) <= 0) {
                    AbDialogUtil.showAlertDialog(getActivity(), "提示", "登录后可以发布求购，现在登录？", new AbAlertDialogFragment.AbDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    });
                } else {
                    startActivity(new Intent(getActivity(), BuysAddActivity.class));
                }
            }
        });
        abPullToRefreshView.setOnHeaderRefreshListener(this);
        abPullToRefreshView.setOnFooterLoadListener(this);
        abPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        abPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mAdapter = new BuysAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
//                    startActivity(new Intent(getActivity(), BuysDetailActivity.class).putExtra(App.MODEL_NAME,adapter.getItem(position)));
//                    startActivity(new Intent(mActivity, BuysReDetailActivity.class).putExtra(App.MODEL_NAME,adapter.getItem(position)));
//                    startActivity(new Intent(getActivity(), BuysListActivity.class).putExtra(Constants.MODEL_NAME,mAdapter.getItem(position)));
                }
            }
        });
        mAdapter.setCallOnClickListener(new BuysAdapter.ICallOnClickListener() {
            @Override
            public void onClick(String phone) {
                final String phone1 = phone;
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.dialog_contact_the_seller, null);
                Button btnQQ = ButterKnife.findById(view, R.id.btnQQ);
                Button btnPhone = ButterKnife.findById(view, R.id.btnPhone);
                Button btnMessage = ButterKnife.findById(view, R.id.btnMessage);
                Button btnEsc = ButterKnife.findById(view, R.id.btnEsc);
                btnEsc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AbDialogUtil.removeDialog(getActivity());
                    }
                });
                btnQQ.setVisibility(View.GONE);
                btnPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((PermissionActivity) getActivity()).checkPermission(
                                new PermissionActivity.CheckPermListener() {
                                    @Override
                                    public void superPermission() {
                                        AbDialogUtil.showAlertDialog(getActivity(), "确认呼叫", phone1
                                                , new AbAlertDialogFragment.AbDialogOnClickListener() {
                                                    @Override
                                                    public void onPositiveClick() {
                                                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone1)));
                                                    }

                                                    @Override
                                                    public void onNegativeClick() {
                                                    }
                                                });
                                    }
                                }, R.string.permission_tips, Manifest.permission.CALL_PHONE);
                    }
                });
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((PermissionActivity)getActivity()).checkPermission(new PermissionActivity.CheckPermListener() {
                            @Override
                            public void superPermission() {
                                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone1)));
                            }
                        },R.string.permission_tips,Manifest.permission.SEND_SMS);

                    }
                });
                AbDialogUtil.showDialog(view, Gravity.BOTTOM);
            }
        });
    }

    @Override
    public void setResource() {
        //设置加载的资源
        this.setLoadDrawable(R.mipmap.ic_load);
        this.setLoadMessage("正在查询,请稍候");
        this.setTextColor(Color.BLACK);
        this.setRefreshDrawable(R.drawable.ic_refresh);
        this.setRefreshMessage("请求出错，请点击按钮重试");
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        if (((int) SPUtil.get(getActivity(), Constants.SHARED_ID,0)) <= 0) {
            BuysFragment.this.setRefreshMessage("登录后可以查看求购信息，请登录");
            showRefreshView();
        } else {
            mPresenter.getList(new String[]{"paging","page_no","page_size"}
                    ,new String[]{
                            String.valueOf(true),
                            String.valueOf(1),
                            String.valueOf(mPageSize)},6,true);
        }
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (((int) SPUtil.get(getActivity(), Constants.SHARED_ID,0)) <= 0) {
            BuysFragment.this.setRefreshMessage("登录后可以查看求购信息，请登录");
            showRefreshView();
        } else {
            if (mAdapter.getCount() >= mTotal) {
                abPullToRefreshView.onFooterLoadFinish();
                ToastUtil.showShort(getActivity(), Constants.NOT_DATA_MSG);
            } else {
                mCurrentPage++;
                mPresenter.getList(new String[]{"paging","page_no","page_size"}
                        ,new String[]{
                                String.valueOf(true),
                                String.valueOf(mCurrentPage),
                                String.valueOf(mPageSize)},6,false);
            }
        }
    }

    @Override
    public void onRefreshSuccess(ResBuysList1 resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("BuysServlet first", resultJson.data);
            if (mAdapter != null)
                mAdapter.update(resultJson.data);
            mTotal = resultJson.total;
            if (mTotal > 0) {
                //显示内容
                showContentView();
            } else {
                //显示重试的框
                BuysFragment.this.setRefreshMessage("暂无数据，请点击按钮重试");
                showRefreshView();
            }
        } else {
            //显示重试的框
            showRefreshView();
        }
    }

    @Override
    public void onLoadSuccess(ResBuysList1 resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("BuysServlet more", resultJson.data);
            if (mAdapter != null)
                mAdapter.addAll(resultJson.data);
        } else {
            mCurrentPage--;
            ToastUtil.showShort(getActivity(), Constants.NET_OUT_MSG);
        }
    }

    @Override
    public void onRefreshFailure() {
        if (mAdapter.isEmpty()) {
            //显示重试的框
            BuysFragment.this.setRefreshMessage("请求出错，请点击按钮重试");
            if (getActivity() == null) return;
            try {
                showRefreshView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShort(getActivity(), Constants.NET_OUT_MSG);
        }
    }

    @Override
    public void onRefreshFinish() {
        mCurrentPage = 1;
        abPullToRefreshView.onHeaderRefreshFinish();
    }

    @Override
    public void onLoadFailure() {
        mCurrentPage--;
        ToastUtil.showShort(getActivity(), Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
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
