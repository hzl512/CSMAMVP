package com.weicent.android.csmamvp.ui.add;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.loopj.android.http.RequestParams;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.contract.CommodityContract;
import com.weicent.android.csmamvp.data.NetWorkWeb;
import com.weicent.android.csmamvp.data.model.result.Category;
import com.weicent.android.csmamvp.presenter.CommodityPresenter;
import com.weicent.android.csmamvp.ui.list.CategoryListActivity;
import com.weicent.android.csmamvp.ui.other.CropImageActivity;
import com.weicent.android.csmamvp.util.GetPathFromUri4kitkatUtil;
import com.weicent.android.csmamvp.util.MyStringUtil;
import com.weicent.android.csmamvp.util.SPUtil;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 商品添加
 */
public class CommodityAddActivity extends AbActivity implements CommodityContract.AddView{

    @BindView(R.id.textCommodityName)
    EditText textCommodityName;

    @BindView(R.id.textCommodityDetail)
    EditText textCommodityDetail;

    @BindView(R.id.textCommodityAddress)
    EditText textCommodityAddress;

    @BindView(R.id.textCommodityPrice)
    EditText textCommodityPrice;

    @BindView(R.id.textCategoryID)
    TextView textCategoryID;

    @BindView(R.id.spinnerBargain)
    Spinner spinnerBargain;

    @BindView(R.id.textCommodityPhone)
    EditText textCommodityPhone;

    @BindView(R.id.textCommodityQQ)
    EditText textCommodityQQ;

    @BindView(R.id.imgView)
    ImageButton imgView;

    private View avatarView = null;
    private AbTitleBar mAbTitleBar = null;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private String mFileName;
    private String mPath;
    private Category mModel = new Category();
    private CommodityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_commodity_add);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        mPresenter=new CommodityPresenter(this,this);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("发布商品");
        mAbTitleBar.setLogo(R.drawable.button_selector_back);
        mAbTitleBar.setTitleBarBackgroundColor(getResources().getColor(R.color.title_bar_back_ground_color));
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoLine(R.mipmap.line);
        //初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avatarView = mInflater.inflate(R.layout.choose_avatar, null);
                Button albumButton = (Button) avatarView.findViewById(R.id.choose_album);
                Button camButton = (Button) avatarView.findViewById(R.id.choose_cam);
                Button cancelButton = (Button) avatarView.findViewById(R.id.choose_cancel);
                albumButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AbDialogUtil.removeDialog(avatarView);
                        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                                // 从相册中去获取
                                try {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                                } catch (ActivityNotFoundException e) {
                                    AbToastUtil.showToast(CommodityAddActivity.this, "没有找到照片");
                                }
                            }
                        },R.string.permission_tips, Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                });
                camButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AbDialogUtil.removeDialog(avatarView);
                        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                                doPickPhotoAction();
                            }
                        },R.string.permission_tips, Manifest.permission.CAMERA);
                    }

                });
                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AbDialogUtil.removeDialog(avatarView);
                    }

                });
                AbDialogUtil.showDialog(avatarView, Gravity.BOTTOM);
            }
        });
        textCategoryID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CommodityAddActivity.this, CategoryListActivity.class), 1);
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spiner_item_0, new String[]{"可小刀", "不可刀"});
        arrayAdapter.setDropDownViewResource(R.layout.spiner_item_list);//设置Dropdown 布局资源
        spinnerBargain.setPrompt("请选择");
        spinnerBargain.setAdapter(arrayAdapter);
    }

    /**
     * 从照相机获取
     */
    private void doPickPhotoAction() {

        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {
                String status = Environment.getExternalStorageState();
                //判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    doTakePhoto();
                } else {
                    AbToastUtil.showToast(CommodityAddActivity.this, "没有可用的存储卡");
                }
            }
        },R.string.permission_tips,Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            AbToastUtil.showToast(this, "未找到系统相机程序");
        }
    }

    /**
     * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况,
     * 他们启动时是这样的startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                if (mIntent != null) {
                    mModel = mIntent.getParcelableExtra(Constants.MODEL_NAME);
                    textCategoryID.setText(mModel.categoryName);
                }
                break;

            case PHOTO_PICKED_WITH_DATA:
                Uri uri = mIntent.getData();
                String currentFilePath = GetPathFromUri4kitkatUtil.getPath(this,uri);
                AbLogUtil.d(this,"uri ="+uri.toString()+ "存储卡中图片的路径是 = " + currentFilePath);
                if (!AbStrUtil.isEmpty(currentFilePath)) {
                    Intent intent1 = new Intent(this, CropImageActivity.class);
                    intent1.putExtra("PATH", currentFilePath);
                    startActivityForResult(intent1, CAMERA_CROP_DATA);
                } else {
                    AbToastUtil.showToast(this, "未在存储卡中找到这个文件");
                }
                break;
            case CAMERA_WITH_DATA:
                AbLogUtil.d(this, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
                String currentFilePath2 = mCurrentPhotoFile.getPath();
                Intent intent2 = new Intent(this, CropImageActivity.class);
                intent2.putExtra("PATH", currentFilePath2);
                startActivityForResult(intent2, CAMERA_CROP_DATA);
                break;
            case CAMERA_CROP_DATA:
                String path1 = mIntent.getStringExtra("PATH");
                AbLogUtil.d(this, "裁剪后得到的图片的路径是 = " + path1);
                mPath = path1;
                imgView.setImageBitmap(AbImageUtil.getBitmap(new File(path1)));
                break;
        }
    }

    @OnClick({R.id.btnSure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSure://确定
                if (MyStringUtil.isNullToToast(CommodityAddActivity.this, mPath, "商品图片") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityName.getText().toString(), "商品名称") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityDetail.getText().toString(), "商品详情") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityAddress.getText().toString(), "交易地点") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityPrice.getText().toString(), "价格") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCategoryID.getText().toString(), "类别") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityPhone.getText().toString(), "联系电话") ||
                        MyStringUtil.isNullToToast(CommodityAddActivity.this, textCommodityQQ.getText().toString(), "QQ")
                        ) return;
//                String filePath = Environment.getExternalStorageDirectory() + "/1.png";
                final RequestParams requestParams = new RequestParams();
                try {
                    requestParams.put("file", new File(mPath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                requestParams.put("usersID", String.valueOf(SPUtil.get(this,Constants.SHARED_ID,0)));
                requestParams.put("commodityName", textCommodityName.getText().toString());
                requestParams.put("commodityDetail", textCommodityDetail.getText().toString());
                requestParams.put("commodityAddress", textCommodityAddress.getText().toString());
                requestParams.put("commodityPrice", textCommodityPrice.getText().toString());
                requestParams.put("categoryID", String.valueOf(mModel.id));
                requestParams.put("commodityBargain", spinnerBargain.getSelectedItemPosition());
                requestParams.put("commodityPhone", textCommodityPhone.getText().toString());
                requestParams.put("commodityQQ", textCommodityQQ.getText().toString());
                mPresenter.httpAdd(requestParams);
                break;
        }
    }

    @Override
    public void addSuccess() {
        finish();
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