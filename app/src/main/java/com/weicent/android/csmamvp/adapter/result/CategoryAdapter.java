package com.weicent.android.csmamvp.adapter.result;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weicent.android.csmamvp.R;
import com.weicent.android.csmamvp.adapter.SimpleBaseAdapter;
import com.weicent.android.csmamvp.app.Global;
import com.weicent.android.csmamvp.data.model.result.Category;

import java.util.List;

/**
 * 分类
 */
public class CategoryAdapter extends SimpleBaseAdapter<Category> {
   	
	public CategoryAdapter(Context context, List<Category> data) {
        super(context, data);
    }

    @Override
    public int getItemResource() {
        return R.layout.list_item_category;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Category model=data.get(position);
        ImageView imageView=holder.getView(R.id.imgView);
	    TextView textCategoryName=holder.getView(R.id.textCategoryName);
	    TextView textCategoryRemark=holder.getView(R.id.textCategoryRemark);
        imageView.setBackgroundDrawable(context.getResources().getDrawable(Global.getInstance().getCategoryView(model.id)));
	    textCategoryName.setText(model.categoryName);
	    textCategoryRemark.setText(model.categoryRemark);
        return convertView;
    }
   
}