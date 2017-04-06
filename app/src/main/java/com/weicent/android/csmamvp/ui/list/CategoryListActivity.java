package com.weicent.android.csmamvp.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.BaseActivity;
import com.weicent.android.csmamvp.IBinDing;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.result.CategoryAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CategoryContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.result.list.ResCategoryList;
import com.weicent.android.csmamvp.presenter.CategoryPresenter;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类别列表
 */
public class CategoryListActivity extends BaseActivity implements IBinDing, AbPullToRefreshView.OnHeaderRefreshListener,CategoryContract.View
        , AbPullToRefreshView.OnFooterLoadListener {
    @BindView(R.id.layoutLoading)
    RelativeLayout layoutLoading;
    @BindView(R.id.layoutMsg)
    RelativeLayout layoutMsg;
    @BindView(R.id.textMsg)
    TextView layoutMsgText;
    @BindView(R.id.layoutContext)
    LinearLayout mLayoutContext;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.abPullToRefreshView)
    AbPullToRefreshView abPullToRefreshView;

    private CategoryAdapter mAdapter = null;//数据适配器
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 10;//页大小
    private AbTitleBar mAbTitleBar = null;
    private CategoryPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mPresenter=new CategoryPresenter(this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("类别");
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
        abPullToRefreshView.setOnHeaderRefreshListener(this);
        abPullToRefreshView.setOnFooterLoadListener(this);
        abPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        abPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mAdapter = new CategoryAdapter(this, null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
                    setResult(RESULT_OK, new Intent().putExtra(Constants.MODEL_NAME, mAdapter.getItem(position)));
                    finish();
                }
            }
        });
        initData();
    }

    @Override
    public void initData() {
        mLayoutContext.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMsg.setVisibility(View.GONE);
        onHeaderRefresh(null);
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        mPresenter.getList(true,1,mPageSize,4,true);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(CategoryListActivity.this, Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(true,mCurrentPage,mPageSize,4,false);
        }
    }

    @Override
    public void onRefreshSuccess(ResCategoryList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CategoryServlet first", resultJson.data);
            if (mAdapter != null)
                mAdapter.update(resultJson.data);
            mTotal = resultJson.total;
            if (resultJson.data.size() > 0) {
                //显示内容
                layoutMsg.setVisibility(View.GONE);
                mLayoutContext.setVisibility(View.VISIBLE);
            } else {
                //显示重试
                layoutMsgText.setText("暂无数据，请点击屏幕重试");
                layoutMsg.setVisibility(View.VISIBLE);
                mLayoutContext.setVisibility(View.GONE);
            }
        } else {
            //显示重试
            layoutMsgText.setText("请求出错，请点击屏幕重试");
            layoutMsg.setVisibility(View.VISIBLE);
            mLayoutContext.setVisibility(View.GONE);
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
        }
    }

    @Override
    public void onRefreshFailure() {
        if (mAdapter.isEmpty()) {
            layoutMsgText.setText("请求出错，请点击屏幕重试");
            layoutMsg.setVisibility(View.VISIBLE);
            mLayoutContext.setVisibility(View.GONE);
        } else {
            ToastUtil.showShort(this, Constants.NET_OUT_MSG);
        }
    }

    @Override
    public void onRefreshFinish() {
        mCurrentPage = 1;
        abPullToRefreshView.onHeaderRefreshFinish();
        layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFailure() {
        mCurrentPage--;
        ToastUtil.showShort(this, Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
    }

}