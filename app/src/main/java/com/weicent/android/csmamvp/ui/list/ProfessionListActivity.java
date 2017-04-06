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
import com.weicent.android.csmamvp.adapter.result.ProfessionAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.ProfessionContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Departments;
import com.weicent.android.csmamvp.data.result.list.ResProfessionList;
import com.weicent.android.csmamvp.presenter.ProfessionPresenter;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 专业列表
 */
public class ProfessionListActivity extends BaseActivity implements IBinDing, AbPullToRefreshView.OnHeaderRefreshListener
        , AbPullToRefreshView.OnFooterLoadListener,ProfessionContract.View {

    @BindView(R.id.layoutLoading)
    RelativeLayout layoutLoading;
    @BindView(R.id.layoutMsg)
    RelativeLayout layoutMsg;
    @BindView(R.id.textMsg)
    TextView textMsg;
    @BindView(R.id.layoutContext)
    LinearLayout layoutContext;

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.abPullToRefreshView)
    AbPullToRefreshView abPullToRefreshView;

    private ProfessionAdapter mAdapter = null;//数据适配器
    private ProfessionPresenter mPresenter;
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 10;//页大小

    private AbTitleBar mAbTitleBar = null;
    private Departments mDepartments = new Departments();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_profession_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mPresenter=new ProfessionPresenter(this);
        mDepartments = getIntent().getParcelableExtra(Constants.MODEL_NAME);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(mDepartments.departmentsName + "专业列表");
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
        mAdapter = new ProfessionAdapter(this, null);
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
        layoutContext.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMsg.setVisibility(View.GONE);
        onHeaderRefresh(null);
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        mPresenter.getList(true,1,mPageSize,mDepartments.id,5,true);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(ProfessionListActivity.this, Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(true,mCurrentPage,mPageSize,mDepartments.id,5,true);
        }
    }

    @Override
    public void onRefreshSuccess(ResProfessionList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("ProfessionServlet first", resultJson.data);
            if (mAdapter != null)
                mAdapter.update(resultJson.data);
            mTotal = resultJson.total;
            if (resultJson.data.size() > 0) {
                //显示内容
                layoutMsg.setVisibility(View.GONE);
                layoutContext.setVisibility(View.VISIBLE);
            } else {
                //显示重试
                textMsg.setText("暂无数据，请点击屏幕重试");
                layoutMsg.setVisibility(View.VISIBLE);
                layoutContext.setVisibility(View.GONE);
            }
        } else {
            //显示重试
            textMsg.setText("请求出错，请点击屏幕重试");
            layoutMsg.setVisibility(View.VISIBLE);
            layoutContext.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadSuccess(ResProfessionList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("ProfessionServlet more", resultJson.data);
            if (mAdapter != null)
                mAdapter.addAll(resultJson.data);
        } else {
            mCurrentPage--;
        }
    }

    @Override
    public void onRefreshFailure() {
        if (mAdapter.isEmpty()) {
            textMsg.setText("请求出错，请点击屏幕重试");
            layoutMsg.setVisibility(View.VISIBLE);
            layoutContext.setVisibility(View.GONE);
        } else {
            ToastUtil.showShort(ProfessionListActivity.this, Constants.NET_OUT_MSG);
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
        ToastUtil.showShort(ProfessionListActivity.this, Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
    }

}

