package com.weicent.android.csmamvp.ui.detail;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDateUtil;
import com.ab.util.AbDialogUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.BaseActivity;
import com.weicent.android.csmamvp.IBinDing;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CommodityContract;
import com.weicent.android.csmamvp.data.BaseResult;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Commodity1;
import com.weicent.android.csmamvp.data.result.model.ResCommodity;
import com.weicent.android.csmamvp.presenter.CommodityPresenter;
import com.weicent.android.csmamvp.ui.LoginActivity;
import com.weicent.android.csmamvp.util.SPUtil;
import com.weicent.android.csmamvp.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 详情
 */
public class CommodityDetailActivity extends BaseActivity implements IBinDing ,CommodityContract.DetailView{

    @BindView(R.id.textCommodityName)
    TextView textCommodityName;

    @BindView(R.id.textCommodityDetail)
    TextView textCommodityDetail;

    @BindView(R.id.textCommodityAddress)
    TextView textCommodityAddress;

    @BindView(R.id.textCommodityPrice)
    TextView textCommodityPrice;

    @BindView(R.id.textCommodityBargain)
    TextView textCommodityBargain;

    @BindView(R.id.textCommodityAddTime)
    TextView textCommodityAddTime;

    @BindView(R.id.textCommodityViews)
    TextView textCommodityViews;

    @BindView(R.id.layoutLoading)
    RelativeLayout layoutLoading;
    @BindView(R.id.layoutMsg)
    RelativeLayout layoutMsg;
    @BindView(R.id.layoutContext)
    LinearLayout layoutContext;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.imgView)
    ImageView imgView;
    @BindView(R.id.textUsersDetail)
    TextView textUsersDetail;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;

    private AbTitleBar mAbTitleBar = null;
    private CommodityPresenter mPresenter;
    private Commodity1 mModel = new Commodity1();
    //图片下载器
    private AbImageLoader mAbImageLoader = null;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_commodity_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mType = getIntent().getIntExtra("type", 0);
        if (mType==1){
            layoutBottom.setVisibility(View.GONE);
        }
        mPresenter=new CommodityPresenter(this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("商品详情");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.title_bar_back_ground_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
        mModel = getIntent().getParcelableExtra(Constants.MODEL_NAME);
        initData();
    }

    @Override
    public void initData() {
        layoutContext.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMsg.setVisibility(View.GONE);
        mPresenter.httpGetDetail(mModel.id,6);
    }

    @Override
    public void httpGetDetailOnSuccess(ResCommodity resultJson) {
        if (resultJson.errorcode == 0) {
            NetWorkWeb.getInstance().doLogResultModelString("UsersServlet first", resultJson.data);
            setValue(resultJson.data);
        } else {
            layoutMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void httpGetDetailOnFailure() {
        ToastUtil.showShort(CommodityDetailActivity.this,Constants.NET_OUT_MSG);
        layoutMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void httpGetDetailOnFinish() {
        layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void httpUpdateViewsSuccess(BaseResult resultJson,int views) {
        if (resultJson.errorcode == 0) {
            textCommodityViews.setText("浏览次数 " + String.valueOf(views + 1));
        }
    }

    private void setValue(Commodity1 data) {
        layoutContext.setVisibility(View.VISIBLE);
        //图片下载器
        mAbImageLoader = AbImageLoader.getInstance(this);
        mAbImageLoader.setDesiredWidth(200);
        mAbImageLoader.setDesiredHeight(200);
        mAbImageLoader.setLoadingImage(R.mipmap.image_loading);
        mAbImageLoader.setErrorImage(R.mipmap.image_error);
        mAbImageLoader.setEmptyImage(R.mipmap.image_empty);
        mAbImageLoader.display(imgView, progressBar, Constants.BASE_URL + mModel.commodityImageUrl);

        textCommodityName.setText(mModel.commodityName);
        textUsersDetail.setText(mModel.usersDetail);
        textCommodityDetail.setText(mModel.commodityDetail);
        textCommodityAddress.setText(mModel.commodityAddress);
        textCommodityPrice.setText(mModel.commodityPrice);
        textCommodityBargain.setText(mModel.commodityBargain == 0 ? "可刀" : "不刀");
        textCommodityAddTime.setText(AbDateUtil.formatDateStr2Desc(mModel.commodityAddTime, AbDateUtil.dateFormatYMDHM2));
        textCommodityViews.setText("浏览次数 " + String.valueOf(data.commodityViews));
        mPresenter.httpUpdateViews(mModel.id,data.commodityViews,5);
    }

    @OnClick({R.id.btnContactTheSeller})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContactTheSeller:
                if ((int) SPUtil.get(this,Constants.SHARED_ID,0)<= 0) {
                    AbDialogUtil.showAlertDialog(this, "提示", "登录后可以查看联系方式，现在登录？", new AbAlertDialogFragment.AbDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            startActivity(new Intent(CommodityDetailActivity.this, LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }

                        @Override
                        public void onNegativeClick() {
                        }
                    });
                } else {
                    LayoutInflater layoutInflater = LayoutInflater.from(this);
                    final View view1 = layoutInflater.inflate(R.layout.dialog_contact_the_seller, null);
                    Button btnQQ = ButterKnife.findById(view1, R.id.btnQQ);
                    Button btnPhone = ButterKnife.findById(view1, R.id.btnPhone);
                    Button btnMessage = ButterKnife.findById(view1, R.id.btnMessage);
                    Button btnEsc = ButterKnife.findById(view1, R.id.btnEsc);
                    btnEsc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AbDialogUtil.removeDialog(view1);
                        }
                    });
                    btnQQ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //复制短信号码至粘贴板
                            ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            cmb.setText(String.valueOf(mModel.commodityQQ));
                            ToastUtil.showLong(CommodityDetailActivity.this,"已复制QQ号码至粘贴板");
                        }
                    });
                    btnPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            checkPermission(new CheckPermListener() {
                                @Override
                                public void superPermission() {
                                    AbDialogUtil.showAlertDialog(CommodityDetailActivity.this, "确认呼叫", String.valueOf(mModel.commodityPhone)
                                            , new AbAlertDialogFragment.AbDialogOnClickListener() {
                                                @Override
                                                public void onPositiveClick() {
                                                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + String.valueOf(mModel.commodityPhone))));
                                                }

                                                @Override
                                                public void onNegativeClick() {
                                                }
                                            });
                                }
                            },R.string.permission_tips,Manifest.permission.CALL_PHONE);

                        }
                    });
                    btnMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermission(new CheckPermListener() {
                                @Override
                                public void superPermission() {
                                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + String.valueOf(mModel.commodityPhone))));
                                }
                            },R.string.permission_tips, Manifest.permission.SEND_SMS);
                        }
                    });
                    AbDialogUtil.showDialog(view1, Gravity.BOTTOM);
                }
                break;
        }
    }

}