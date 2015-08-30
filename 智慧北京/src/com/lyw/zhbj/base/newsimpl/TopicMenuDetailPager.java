package com.lyw.zhbj.base.newsimpl;

import com.lyw.zhbj.base.MenuDetailBasePager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class TopicMenuDetailPager extends MenuDetailBasePager{
	
	public TopicMenuDetailPager(Context context) {
		super(context);
	}

	public View initView() {
		TextView tv = new TextView(context);
		tv.setText("专题菜单页面");
		tv.setTextSize(23);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}
	
}
