package com.lyw.zhbj.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lyw.zhbj.base.BasePage;


/**
 * 首页的页面
 */
public class HomePager extends BasePage {

	public HomePager(Context context) {
		super(context);
	}

	@Override
	public void initPageData() {
		System.out.println("首页数据初始化了.");
		tv_title.setText("智慧北京");
		ib_menu.setVisibility(View.GONE);
		
		TextView tv = new TextView(context);
		tv.setText("首页");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
