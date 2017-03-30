package com.weicent.android.csmamvp.adapter.result;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.SimpleBaseAdapter;
import com.weicent.android.csmamvp.app.Constants;
import com.weicent.android.csmamvp.data.model.result.Commodity1;

import java.util.List;

/**
 * 
 */
public class CommodityAdapter extends SimpleBaseAdapter<Commodity1> {
   	
	//图片下载器
	private AbImageLoader mAbImageLoader = null;
	public CommodityAdapter(Context context, List<Commodity1> data) {
        super(context, data);
		//图片下载器
		mAbImageLoader = AbImageLoader.getInstance(context);
		mAbImageLoader.setDesiredWidth(200);
		mAbImageLoader.setDesiredHeight(200);
		mAbImageLoader.setLoadingImage(R.mipmap.image_loading);
		mAbImageLoader.setErrorImage(R.mipmap.image_error);
		mAbImageLoader.setEmptyImage(R.mipmap.image_empty);
    }

    @Override
    public int getItemResource() {
        return R.layout.list_item_commodity;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Commodity1 model=data.get(position);
		ProgressBar progressBar=holder.getView(R.id.progressBar);
		ImageView imgView=holder.getView(R.id.imgView);
	    TextView textCommodityName=holder.getView(R.id.textCommodityName);
	    TextView textCommodityAddress=holder.getView(R.id.textCommodityAddress);
	    TextView textCommodityPrice=holder.getView(R.id.textCommodityPrice);
	    TextView textUsersID=holder.getView(R.id.textUsersID);
	    textCommodityName.setText(model.commodityName);
	    textCommodityAddress.setText(model.commodityAddress);
	    textCommodityPrice.setText(model.commodityPrice);
		textUsersID.setText(model.usersProfessionNameGrade);
		//图片的下载
		mAbImageLoader.display(imgView, progressBar, Constants.BASE_URL + model.commodityImageUrl);
        return convertView;
    }

	public void updateSingleRow(GridView gridView,Integer id){
		if (gridView!=null){
			int start=gridView.getFirstVisiblePosition();
			for(int i = start,j=gridView.getLastVisiblePosition();i<=j;i++){
				if (id==((Commodity1)gridView.getItemAtPosition(i)).id){
					((Commodity1)gridView.getItemAtPosition(i)).commodityName="123456";
					View view=gridView.getChildAt(i-start);//因为start是当前载入列表数据中首个position又是在变化中,所以要减去这个，获取其变动的位置
					getView(i,view,gridView);
				}
			}
		}
	}

   
}