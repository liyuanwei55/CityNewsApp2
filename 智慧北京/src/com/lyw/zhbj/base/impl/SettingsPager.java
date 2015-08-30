package com.lyw.zhbj.base.impl;

import com.lyw.zhbj.base.BasePage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


/**
 * @author andong
 * 设置的页面
 */
public class SettingsPager extends BasePage{

	public SettingsPager(Context context) {
		super(context);
	}

	@Override
	public void initPageData() {
		System.out.println("设置数据初始化了.");
		tv_title.setText("设置");
		ib_menu.setVisibility(View.GONE);
		
		TextView tv = new TextView(context);
		tv.setText("设置");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
