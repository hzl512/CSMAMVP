package com.weicent.android.csmamvp.ui.add;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ab.view.titlebar.AbTitleBar;
import com.weicent.android.csmamvp.BaseActivity;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.BuysContract;
import com.weicent.android.csmamvp.presenter.BuysPresenter;
import com.weicent.android.csmamvp.util.SPUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加求购
 */
public class BuysAddActivity extends BaseActivity implements BuysContract.AddView{

    @BindView(R.id.textBuysName)
    EditText textBuysName;
    @BindView(R.id.textBuysPrice)
    EditText textBuysPrice;
    @BindView(R.id.textBuysAddress)
    EditText textBuysAddress;
    @BindView(R.id.textBuysDetail)
    EditText textBuysDetail;
    @BindView(R.id.textBuysPhone)
    EditText textBuysPhone;
    @BindView(R.id.textBuysQQ)
    EditText textBuysQQ;

    private AbTitleBar mAbTitleBar = null;
    private BuysPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_buys_add);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        mPresenter=new BuysPresenter(this,this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("添加求购");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.title_bar_back_ground_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
    }

    @OnClick({R.id.btnEsc, R.id.btnSure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEsc://清空
                textBuysName.setText("");
                textBuysPrice.setText("");
                textBuysAddress.setText("");
                textBuysDetail.setText("");
                textBuysPhone.setText("");
                textBuysQQ.setText("");
                break;
            case R.id.btnSure://确定
                mPresenter.httpAdd(new String[]{
                        "usersID","buysName","buysPrice","buysAddress","buysDetail","buysPhone","buysQQ"
                },new String[]{
                        String.valueOf(SPUtil.get(this, Constants.SHARED_ID, 0)),
                        textBuysName.getText().toString().trim(),
                        textBuysPrice.getText().toString().trim(),
                        textBuysAddress.getText().toString().trim(),
                        textBuysDetail.getText().toString().trim(),
                        textBuysPhone.getText().toString().trim(),
                        textBuysQQ.getText().toString().trim()
                },1);
                break;
        }
    }

    @Override
    public void onSuccess() {
        finish();
    }

}