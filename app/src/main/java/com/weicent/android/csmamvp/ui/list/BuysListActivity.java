package com.weicent.android.csmamvp.ui.list;

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
import com.weicent.android.csmamvp.adapter.result.BuysAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.BuysContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.result.list.ResBuysList1;
import com.weicent.android.csmamvp.presenter.BuysPresenter;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的求购
 */
public class BuysListActivity extends BaseActivity implements IBinDing, AbPullToRefreshView.OnHeaderRefreshListener,BuysContract.View
        , AbPullToRefreshView.OnFooterLoadListener {

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

    private BuysAdapter mAdapter = null;//数据适配器
    private int mCurrentPage = 1;//当前页
    private int mTotal = 0;//页总数
    private int mPageSize = 5;//页大小
    private AbTitleBar mAbTitleBar = null;
    private BuysPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_buys_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mPresenter=new BuysPresenter(this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("我的求购");
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
        mAdapter = new BuysAdapter(this, null);
        listView.setAdapter(mAdapter);
        mAdapter.setType(1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < mAdapter.getCount()) {
                    //startActivity(new Intent(BuysListActivity.this, BuysDetailActivity.class).putExtra(App.MODEL_NAME,adapter.getItem(position)));
                    //startActivity(new Intent(BuysListActivity.this, BuysReDetailActivity.class).putExtra(App.MODEL_NAME,adapter.getItem(position)));
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
        mPresenter.getList(new String[]{"true","page_no","page_size","id"}
                ,new String[]{
                        String.valueOf(true),
                        String.valueOf(1),
                        String.valueOf(mPageSize),
                        String.valueOf(SPUtil.get(this,Constants.INTENT_KEY_ID,0)),
                },7,true);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        if (mAdapter.getCount() >= mTotal) {
            abPullToRefreshView.onFooterLoadFinish();
            ToastUtil.showShort(BuysListActivity.this, Constants.NOT_DATA_MSG);
        } else {
            mCurrentPage++;
            mPresenter.getList(new String[]{"true","page_no","page_size","id"}
                    ,new String[]{
                            String.valueOf(true),
                            String.valueOf(mCurrentPage),
                            String.valueOf(mPageSize),
                            String.valueOf(SPUtil.get(this,Constants.INTENT_KEY_ID,0)),
                    },7,true);
        }
    }

    @Override
    public void onRefreshSuccess(ResBuysList1 resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("BuysServlet first", resultJson.data);
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
    public void onLoadSuccess(ResBuysList1 resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultDataString("BuysServlet more", resultJson.data);
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
            ToastUtil.showShort(BuysListActivity.this, Constants.NET_OUT_MSG);
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
        ToastUtil.showShort(BuysListActivity.this, Constants.NET_OUT_MSG);
    }

    @Override
    public void onLoadFinish() {
        abPullToRefreshView.onFooterLoadFinish();
    }
}