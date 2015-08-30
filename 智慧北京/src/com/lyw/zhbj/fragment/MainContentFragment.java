package com.lyw.zhbj.fragment;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lyw.zhbj.MainUIActivity;
import com.lyw.zhbj.R;
import com.lyw.zhbj.base.BasePage;
import com.lyw.zhbj.base.impl.GovaffirsPager;
import com.lyw.zhbj.base.impl.HomePager;
import com.lyw.zhbj.base.impl.NewsCenterPager;
import com.lyw.zhbj.base.impl.SettingsPager;
import com.lyw.zhbj.base.impl.SmartServicePager;
import com.lyw.zhbj.view.NoScrollViewPager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 *	首页，新闻中国，智慧服务，政务，设置 五个底部标签如何切换?
 *		viewpager的五个view相互切换
 *	impl包中的五个类提供相应的view
 */
public class MainContentFragment extends Fragment implements OnCheckedChangeListener {
	
	private NoScrollViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private List<BasePage> pagerList;
	
	private Activity activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_content_fragment, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initFragmentView();
		initFragmentData();//初始化fragment数据
	}
	/**
	 * 该fragment包括两部分：底部栏RadioGroup与是上部的ViewPager
	 */
	private void initFragmentView() {
		activity = getActivity();
		
		mRadioGroup = (RadioGroup) activity.findViewById(R.id.rg_content_fragment);
		mViewPager = (NoScrollViewPager) activity.findViewById(R.id.vp_content_fragment);
		
		mRadioGroup.setOnCheckedChangeListener(this);
	}

	private void initFragmentData() {
		pagerList = new ArrayList<BasePage>(); 
		pagerList.add(new HomePager(activity));
		pagerList.add(new NewsCenterPager(activity));
		pagerList.add(new SmartServicePager(activity));
		pagerList.add(new GovaffirsPager(activity));
		pagerList.add(new SettingsPager(activity));
		
		ContentAdapter mAdapter = new ContentAdapter();
		mViewPager.setAdapter(mAdapter);
		
		//默认首页数据
		mRadioGroup.check(R.id.rb_content_fragment_home); // 设置默认选中的是home页签
		pagerList.get(0).initPageData(); // 初始化首页的数据
		((MainUIActivity) activity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}
	
	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			System.out.println("预加载: " + position);
			BasePage pager = pagerList.get(position);
			View rootView = pager.getRootView();//lyw 在这里实现了数据与视图的分离，viewpage还是会预加载下一页，但这只是个空架子，没有数据
			container.addView(rootView);
//			pager.initData();
			return rootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
	/**
	 * RadioGroup的监听事件
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = -1;
		switch (checkedId) {
		case R.id.rb_content_fragment_home:
			index = 0;
			break;
		case R.id.rb_content_fragment_newscenter:
			index = 1;
			break;
		case R.id.rb_content_fragment_smartservice:
			index = 2;
			break;
		case R.id.rb_content_fragment_govaffairs:
			index = 3;
			break;
		case R.id.rb_content_fragment_settings:
			index = 4;
			break;
		default:
			break;
		}
		
		mViewPager.setCurrentItem(index, false);//false去掉滑动效果
		pagerList.get(index).initPageData(); // 初始化数据
		
		if(index == 0 || index == 4) {
			// 菜单不可用
			//getSlidingMenu()   Gets the SlidingMenu associated with this activity.
			((MainUIActivity) activity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		} else {
			((MainUIActivity) activity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
	}
	
	/**
	 * 获取新闻中心页面
	 * @return
	 */
	public NewsCenterPager getNewsCenterPager(){
		return (NewsCenterPager) pagerList.get(1);
	}
}
