package com.lyw.zhbj.base.newsimpl;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lyw.zhbj.MainUIActivity;
import com.lyw.zhbj.R;
import com.lyw.zhbj.base.MenuDetailBasePager;
import com.lyw.zhbj.base.NewsTabIndicatorBasePager;
import com.lyw.zhbj.base.newstab.NewsTabIndicatorDetailPager;
import com.lyw.zhbj.domin.NewsCenterBean.ChildRen;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * ����ҳǩ���˵� ���ŵ�ʵ�� 
 * @author liyuanwei55
 *
 */
public class NewsCenterMenuDetailPager extends MenuDetailBasePager implements OnClickListener, OnPageChangeListener{
	private TabPageIndicator tabPageIndicator;
	private ViewPager viewPager;
	private List<ChildRen> childrenList;//������viewpageIndicator��������Ϣ
	private ImageButton btn_next;//viewpageIndicator����һ����ǩ
	private List<NewsTabIndicatorDetailPager> basePager;//viewPager��view �������й�������...
	
	public NewsCenterMenuDetailPager(Context context) {
		super(context);
	}

	public NewsCenterMenuDetailPager(Context context,
			List<ChildRen> children) {
		super(context);//����Ĺ��캯����Ĭ�ϵ��ø����޲εĹ��췽�������ڸ���û���޲ι��죬��������Ҫ��ʽ�ĵ���
		this.childrenList = children;
	}
	/**
	 * �����и÷����Ǹ����󷽷����������ṩһ��view�ļ���
	 */
	public View initView() {
		View view = View.inflate(context,R.layout.newscenter_menudetai, null);
		tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.tpi_news_menu);
		viewPager = (ViewPager) view.findViewById(R.id.vp_news_menu_content);
		
		btn_next = (ImageButton) view.findViewById(R.id.ib_news_menu_next_tab);
		return view;
	}
	/**
	 * ���ⲿ���ã������ǳ�ʵ���view,ʹ����������ҳ��
	 */
	@Override
	public void initData() {
		//׼����newPager��view
		basePager = new ArrayList<NewsTabIndicatorDetailPager>();
		for (int i = 0; i < childrenList.size(); i++) {
			basePager.add(new NewsTabIndicatorDetailPager(context, childrenList.get(i)));
		}
		
		NewsMenuAdapter mAdapter = new NewsMenuAdapter();
		viewPager.setAdapter(mAdapter);
		
		tabPageIndicator.setViewPager(viewPager);//��pageIndicator��viewPage��������.Bind the indicator to a ViewPager
		tabPageIndicator.setOnPageChangeListener(this);//lyw �������õ���tabPageIndicator�ļ����¼�
		//tabPageIndicator.setCurrentItem(0);
		
		btn_next.setOnClickListener(this);
		
	}
	
	class NewsMenuAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return childrenList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
//			TextView view = new TextView(context);
//			view.setText(childrenList.get(position).title);
//			view.setTextSize(30);
//			view.setTextColor(Color.RED);
			NewsTabIndicatorDetailPager newsTabIndicatorDetailPager = basePager.get(position);
			View view = newsTabIndicatorDetailPager.initView();
			container.addView(view);
			//newsTabIndicatorDetailPager.initData();
			if(position==0){
				newsTabIndicatorDetailPager.initData();
			}
			return view;
		}

		/**
		 * ���ص��ַ�������ΪTabPageIndicator��Ӧposition��ҳǩ������չʾ.
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			return childrenList.get(position).title;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_news_menu_next_tab:
			int currentItem = viewPager.getCurrentItem();
			viewPager.setCurrentItem(currentItem+1, false);
			break;

		default:
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		isEnableSlidingMenu(position==0);//���������󻬶�ʱ���ֲ�����SlidingMenu�����⣬һ����viewpageIndicator,һ����viewpageIndicator��Ӧ��viewpager	
		
		//��ʼ������
		basePager.get(position).initData();//lyw
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	/**
	 * �Ƿ����ò໬�˵�
	 * @param isEnable true����
	 */
	private void isEnableSlidingMenu(boolean isEnable) {
		if(isEnable) {
			((MainUIActivity)context).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			((MainUIActivity)context).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
}
