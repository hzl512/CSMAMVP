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

import com.ab.view.pullview.AbPullToRefreshView;
import com.weicent.android.csmamvp.BaseFragment;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.result.CategoryAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CategoryContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.result.list.ResCategoryList;
import com.weicent.android.csmamvp.presenter.CategoryPresenter;
import com.weicent.android.csmamvp.ui.list.CommodityListActivity;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.ButterKnife;

/**
 * 分类列表
 */
public class CategoryFragment extends BaseFragment implements AbPullToRefreshView.OnHeaderRefreshListener, CategoryContract.View
        , AbPullToRefreshView.OnFooterLoadListener {
    GridView gridView;
    AbPullToRefreshView abPullToRefreshView;
    TextView textTitle;
    private CategoryPresenter mPresenter;
    private CategoryAdapter mAdapter = null;//数据适配器
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 10;//页大小

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, null);
        abPullToRefreshView = ButterKnife.findById(view, R.id.abPullToRefreshView);
        gridView = ButterKnife.findById(view, R.id.gridView);
        textTitle = ButterKnife.findById(view, R.id.textTitle);
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
        textTitle.setText("分类");
        abPullToRefreshView.setOnHeaderRefreshListener(this);
        abPullToRefreshView.setOnFooterLoadListener(this);
        abPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        abPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mPresenter=new CategoryPresenter(this);
        mAdapter = new CategoryAdapter(getActivity(), null);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
                    startActivity(new Intent(getActivity(), CommodityListActivity.class).putExtra(Constants.MODEL_NAME,mAdapter.getItem(position)));
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
        mPresenter.getList(true, 1, mPageSize, 4, true);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(getActivity(), Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(true, mCurrentPage, mPageSize, 4, false);
        }

    }

    @Override
    public void onRefreshSuccess(ResCategoryList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CategoryServlet first", resultJson.data);
            if (mAdapter != null)
                mAdapter.update(resultJson.data);
            mTotal = resultJson.total;
            if (mTotal > 0) {
                //显示内容
                showContentView();
            } else {
                //显示重试的框
                CategoryFragment.this.setRefreshMessage("暂无数据，请点击按钮重试");
                showRefreshView();
            }
        } else {
            //显示重试的框
            showRefreshView();
        }
    }

    @Override
    public void onLoadSuccess(ResCategoryList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CategoryServlet more", resultJson.data);
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
            CategoryFragment.this.setRefreshMessage("请求出错，请点击按钮重试");
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