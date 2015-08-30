package com.lyw.zhbj.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lyw.zhbj.base.BasePage;


/**
 * ��ҳ��ҳ��
 */
public class HomePager extends BasePage {

	public HomePager(Context context) {
		super(context);
	}

	@Override
	public void initPageData() {
		System.out.println("��ҳ���ݳ�ʼ����.");
		tv_title.setText("�ǻ۱���");
		ib_menu.setVisibility(View.GONE);
		
		TextView tv = new TextView(context);
		tv.setText("��ҳ");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
