package com.lyw.zhbj.base;

import android.content.Context;
import android.view.View;

public abstract class NewsTabIndicatorBasePager {
	public Context context;
	private View rootView;

	public NewsTabIndicatorBasePager(Context context) {
		this.context = context;
		rootView = initView();
	}

	public abstract View initView();

	public View getRootView() {
		return rootView;
	}

	/**
	 * ������Ҫ���Ǵ˷���, ʵ���Լ������ݳ�ʼ��.
	 */
	public void initData() {

	}
}
