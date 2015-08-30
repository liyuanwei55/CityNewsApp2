package com.lyw.zhbj;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lyw.zhbj.fragment.LeftMenuFragment;
import com.lyw.zhbj.fragment.MainContentFragment;

public class MainUIActivity extends SlidingFragmentActivity {
	// 左侧菜单fragment的tag
	private final String LEFT_MENU_FRAGMENT_TAG = "left_menu";
	// 主界面fragment的tag
	private final String MAIN_CONTENT_FRAGMENT_TAG = "main_content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_content);
		// 设置左侧菜单布局
		setBehindContentView(R.layout.left_menu);
		// 设置右侧菜单布局
		SlidingMenu slidingMenu = getSlidingMenu(); // 获取菜单配置和控制对象
		// 设置左右菜单都可用.
		slidingMenu.setMode(SlidingMenu.LEFT);
		// 设置整个屏幕都可以滑动出来菜单
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 设置主界面可以留在屏幕上的宽度
		slidingMenu.setBehindOffset(400);

		initFragment();
	}

	private void initFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction t = fragmentManager.beginTransaction();

		t.replace(R.id.rl_main_content, new MainContentFragment(),
				MAIN_CONTENT_FRAGMENT_TAG);
		t.replace(R.id.rl_left_menu, new LeftMenuFragment(),
				LEFT_MENU_FRAGMENT_TAG);

		t.commit();

	}
	/**
	 * 获取左侧LeftMenuFragment
	 */
	public LeftMenuFragment getLeftMenuFragment(){
		return (LeftMenuFragment) getFragmentManager().findFragmentByTag(LEFT_MENU_FRAGMENT_TAG);
	}
	/**
	 * 获取MainContentFragment
	 */
	public MainContentFragment getMainContentFragment(){
		return (MainContentFragment) getFragmentManager().findFragmentByTag(MAIN_CONTENT_FRAGMENT_TAG);
	}
}
