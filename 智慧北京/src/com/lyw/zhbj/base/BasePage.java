package com.lyw.zhbj.base;


import com.lyw.zhbj.R;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class BasePage {
	public Context context;
	
	public TextView tv_title;
	public ImageButton ib_menu;
	public FrameLayout fl_content;
	public ImageButton ListOrGrid;//组图页面listview与gridview切换的按钮
	
	private View rootView;
	
	public BasePage(Context context){
		this.context = context;
		
		rootView = initView();
	}
	
	private View initView() {
		View view = View.inflate(context, R.layout.tab_base_pager, null);
		tv_title = (TextView) view.findViewById(R.id.tv_title_bar_title);
		ib_menu = (ImageButton) view.findViewById(R.id.ib_title_bar_menu);
		fl_content = (FrameLayout) view.findViewById(R.id.fl_tab_base_pager_content);
		ListOrGrid = (ImageButton) view.findViewById(R.id.ib_title_bar_list_or_grid);
		return view;
	}
	
	/**
	 * 获得当前页面布局对象
	 * @return
	 */
	public View getRootView() {
		return rootView;
	}
	
	public void initPageData(){
		
	}
}
