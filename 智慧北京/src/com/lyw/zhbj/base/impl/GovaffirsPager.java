package com.lyw.zhbj.base.impl;

import com.lyw.zhbj.base.BasePage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


/**
 * @author andong
 * 政务的页面
 */
public class GovaffirsPager extends BasePage {

	public GovaffirsPager(Context context) {
		super(context);
	}

	@Override
	public void initPageData() {
		System.out.println("政务数据初始化了.");
		tv_title.setText("政务");
		ib_menu.setVisibility(View.VISIBLE);
		
		TextView tv = new TextView(context);
		tv.setText("政务");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
