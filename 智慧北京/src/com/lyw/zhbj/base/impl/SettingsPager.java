package com.lyw.zhbj.base.impl;

import com.lyw.zhbj.base.BasePage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


/**
 * @author andong
 * ���õ�ҳ��
 */
public class SettingsPager extends BasePage{

	public SettingsPager(Context context) {
		super(context);
	}

	@Override
	public void initPageData() {
		System.out.println("�������ݳ�ʼ����.");
		tv_title.setText("����");
		ib_menu.setVisibility(View.GONE);
		
		TextView tv = new TextView(context);
		tv.setText("����");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		fl_content.addView(tv);
	}
}
