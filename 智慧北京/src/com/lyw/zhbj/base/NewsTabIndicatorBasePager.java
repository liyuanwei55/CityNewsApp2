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
	 * 子类需要覆盖此方法, 实现自己的数据初始化.
	 */
	public void initData() {

	}
}
