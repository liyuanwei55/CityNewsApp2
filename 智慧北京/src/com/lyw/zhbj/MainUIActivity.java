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
	// ���˵�fragment��tag
	private final String LEFT_MENU_FRAGMENT_TAG = "left_menu";
	// ������fragment��tag
	private final String MAIN_CONTENT_FRAGMENT_TAG = "main_content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_content);
		// �������˵�����
		setBehindContentView(R.layout.left_menu);
		// �����Ҳ�˵�����
		SlidingMenu slidingMenu = getSlidingMenu(); // ��ȡ�˵����úͿ��ƶ���
		// �������Ҳ˵�������.
		slidingMenu.setMode(SlidingMenu.LEFT);
		// ����������Ļ�����Ի��������˵�
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// �������������������Ļ�ϵĿ��
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
	 * ��ȡ���LeftMenuFragment
	 */
	public LeftMenuFragment getLeftMenuFragment(){
		return (LeftMenuFragment) getFragmentManager().findFragmentByTag(LEFT_MENU_FRAGMENT_TAG);
	}
	/**
	 * ��ȡMainContentFragment
	 */
	public MainContentFragment getMainContentFragment(){
		return (MainContentFragment) getFragmentManager().findFragmentByTag(MAIN_CONTENT_FRAGMENT_TAG);
	}
}
