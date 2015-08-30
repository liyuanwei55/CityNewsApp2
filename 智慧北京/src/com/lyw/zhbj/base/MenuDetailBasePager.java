package com.lyw.zhbj.base;

import android.content.Context;
import android.view.View;

public abstract class MenuDetailBasePager {

	
	public Context context;
	private View rootView;

	public MenuDetailBasePager(Context context) {
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
