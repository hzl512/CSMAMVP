package com.weicent.android.csmamvp.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.IBinDing;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.result.CommodityAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CommodityContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.request.model.ReqCommodity;
import com.weicent.android.csmamvp.data.result.list.ResCommodityList;
import com.weicent.android.csmamvp.presenter.CommodityPresenter;
import com.weicent.android.csmamvp.ui.detail.CommodityDetailActivity;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的商品
 */
public class MyCommodityListActivity extends AbActivity implements IBinDing, AbPullToRefreshView.OnHeaderRefreshListener,CommodityContract.View
        , AbPullToRefreshView.OnFooterLoadListener {

    @BindView(R.id.layoutLoading)
    RelativeLayout layoutLoading;
    @BindView(R.id.layoutMsg)
    RelativeLayout layoutMsg;
    @BindView(R.id.textMsg)
    TextView textMsg;
    @BindView(R.id.layoutContext)
    LinearLayout layoutContext;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.abPullToRefreshView)
    AbPullToRefreshView abPullToRefreshView;

    private CommodityAdapter mAdapter = null;//数据适配器
    private CommodityPresenter mPresenter;
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 5;//页大小
    private int mType;
    private AbTitleBar mAbTitleBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_my_commodity_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mPresenter=new CommodityPresenter(this);
        mType = getIntent().getIntExtra("type", 0);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("我的商品");
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
        mAdapter = new CommodityAdapter(this, null);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
                    startActivity(new Intent(MyCommodityListActivity.this, CommodityDetailActivity.class)
                            .putExtra(Constants.MODEL_NAME, mAdapter.getItem(position)).putExtra("type", mType));
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
        ReqCommodity reqCommodity = new ReqCommodity();
        reqCommodity.data.setUsersID((int) SPUtil.get(this, Constants.INTENT_KEY_ID, 0));
        reqCommodity.paging = true;
        reqCommodity.page_no = 1;
        reqCommodity.page_size = mPageSize;
        mPresenter.getList(MyStringUtil.createJsonString(reqCommodity), 8,true);

    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        ReqCommodity reqCommodity = new ReqCommodity();
        reqCommodity.data.setUsersID((int) SPUtil.get(this, Constants.INTENT_KEY_ID, 0));
        reqCommodity.paging = true;
        reqCommodity.page_no = mCurrentPage;
        reqCommodity.page_size = mPageSize;
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(MyCommodityListActivity.this, Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(MyStringUtil.createJsonString(reqCommodity), 8,false);
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
    public void onLoadSuccess(ResCommodityList resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("CommodityServlet more", resultJson.data);
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
            ToastUtil.showShort(MyCommodityListActivity.this, Constants.NET_OUT_MSG);
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
        ToastUtil.showShort(MyCommodityListActivity.this, Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
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