package com.weicent.android.csmamvp.ui.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.weicent.android.csmamvp.BaseFragment;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.result.CommodityAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CommodityContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.result.list.ResCommodityList;
import com.weicent.android.csmamvp.presenter.CommodityPresenter;
import com.weicent.android.csmamvp.ui.LoginActivity;
import com.weicent.android.csmamvp.ui.add.CommodityAddActivity;
import com.weicent.android.csmamvp.ui.detail.CommodityDetailActivity;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.ButterKnife;

/**
 * 商品列表
 */
public class CommodityFragment extends BaseFragment implements AbPullToRefreshView.OnHeaderRefreshListener,CommodityContract.View
        , AbPullToRefreshView.OnFooterLoadListener {
    GridView gridView;
    AbPullToRefreshView abPullToRefreshView;
    TextView textTitle,textRight;
    private CommodityPresenter mPresenter;
    private CommodityAdapter mAdapter = null;//数据适配器
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 10;//页大小

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commodity, null);
        abPullToRefreshView = ButterKnife.findById(view, R.id.abPullToRefreshView);
        gridView = ButterKnife.findById(view, R.id.gridView);
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
        mPresenter=new CommodityPresenter(this,getActivity());
        textTitle.setText(R.string.title_index);
        textRight.setVisibility(View.VISIBLE);
        textRight.setText("我要发布");
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAdapter.updateSingleRow(gridView,9);
                if (((int) SPUtil.get(getActivity(), Constants.SHARED_ID,0)) <= 0) {
                    AbDialogUtil.showAlertDialog(getActivity(), "提示", "登录后可以发布商品，现在登录？", new AbAlertDialogFragment.AbDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            startActivity(new Intent(getActivity(), LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    });
                }else {
                    startActivity(new Intent(getActivity(),CommodityAddActivity.class));
                }

            }
        });
//        mPresenter.start();
        abPullToRefreshView.setOnHeaderRefreshListener(this);
        abPullToRefreshView.setOnFooterLoadListener(this);
        abPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        abPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mAdapter = new CommodityAdapter(getActivity(), null);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
                    startActivity(new Intent(getActivity(), CommodityDetailActivity.class).putExtra(Constants.MODEL_NAME, mAdapter.getItem(position)));
                    //startActivity(new Intent(mActivity, CommodityReDetailActivity.class).putExtra(App.MODEL_NAME,mAdapter.getItem(position)));
                    //startActivity(new Intent(mActivity, CommodityListActivity.class).putExtra(App.MODEL_NAME,mAdapter.getItem(position)));
                }
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
        mPresenter.getList(true,1,mPageSize,4,true);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(getActivity(),Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(true,mCurrentPage,mPageSize,4,false);
        }
    }

    @Override
    public void onRefreshSuccess(ResCommodityList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CommodityServlet first", resultJson.data);
            if (mAdapter != null)
                mAdapter.update(resultJson.data);
            mTotal = resultJson.total;
            if (resultJson.data.size() > 0) {
                //显示内容
                showContentView();
            } else {
                //显示重试的框
                CommodityFragment.this.setRefreshMessage("暂无数据，请点击按钮重试");
                showRefreshView();
            }
        } else {
            //显示重试的框
            showRefreshView();
        }
    }

    @Override
    public void onLoadSuccess(ResCommodityList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CommodityServlet more", resultJson.data);
            if (mAdapter != null)
                mAdapter.addAll(resultJson.data);
        } else {
            mCurrentPage--;
            ToastUtil.showShort(getActivity(),Constants.NET_OUT_MSG);
        }
    }

    @Override
    public void onRefreshFailure() {
        if (mAdapter.isEmpty()) {
            //显示重试的框
            CommodityFragment.this.setRefreshMessage("请求出错，请点击按钮重试");
            if (getActivity() == null) return;
            try {
                showRefreshView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showShort(getActivity(),Constants.NET_OUT_MSG);
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
        ToastUtil.showShort(getActivity(),Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (NetWorkWeb.getInstance().getRequestClient()==null){
            return;
        }else {
            NetWorkWeb.getInstance().getRequestClient().cancelAllRequests(true);//关闭所有请求
        }
    }

}
