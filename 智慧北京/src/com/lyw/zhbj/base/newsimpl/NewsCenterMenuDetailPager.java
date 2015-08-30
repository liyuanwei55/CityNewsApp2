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
 * 新闻页签左侧菜单 新闻的实现 
 * @author liyuanwei55
 *
 */
public class NewsCenterMenuDetailPager extends MenuDetailBasePager implements OnClickListener, OnPageChangeListener{
	private TabPageIndicator tabPageIndicator;
	private ViewPager viewPager;
	private List<ChildRen> childrenList;//里面有viewpageIndicator的数据信息
	private ImageButton btn_next;//viewpageIndicator的下一个标签
	private List<NewsTabIndicatorDetailPager> basePager;//viewPager的view 北京，中国，国际...
	
	public NewsCenterMenuDetailPager(Context context) {
		super(context);
	}

	public NewsCenterMenuDetailPager(Context context,
			List<ChildRen> children) {
		super(context);//子类的构造函数会默认调用父类无参的构造方法，由于父类没有无参构造，所以这里要显式的调用
		this.childrenList = children;
	}
	/**
	 * 父类中该方法是个抽象方法，作用是提供一个view的架子
	 */
	public View initView() {
		View view = View.inflate(context,R.layout.newscenter_menudetai, null);
		tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.tpi_news_menu);
		viewPager = (ViewPager) view.findViewById(R.id.vp_news_menu_content);
		
		btn_next = (ImageButton) view.findViewById(R.id.ib_news_menu_next_tab);
		return view;
	}
	/**
	 * 由外部调用，作用是充实这个view,使其变成生动的页面
	 */
	@Override
	public void initData() {
		//准备好newPager的view
		basePager = new ArrayList<NewsTabIndicatorDetailPager>();
		for (int i = 0; i < childrenList.size(); i++) {
			basePager.add(new NewsTabIndicatorDetailPager(context, childrenList.get(i)));
		}
		
		NewsMenuAdapter mAdapter = new NewsMenuAdapter();
		viewPager.setAdapter(mAdapter);
		
		tabPageIndicator.setViewPager(viewPager);//把pageIndicator与viewPage关联起来.Bind the indicator to a ViewPager
		tabPageIndicator.setOnPageChangeListener(this);//lyw 这里设置的是tabPageIndicator的监听事件
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
		 * 返回的字符串会作为TabPageIndicator对应position的页签数据来展示.
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
		isEnableSlidingMenu(position==0);//处理两个左滑动时出现不出现SlidingMenu的问题，一个是viewpageIndicator,一个是viewpageIndicator对应的viewpager	
		
		//初始化数据
		basePager.get(position).initData();//lyw
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	/**
	 * 是否启用侧滑菜单
	 * @param isEnable true启用
	 */
	private void isEnableSlidingMenu(boolean isEnable) {
		if(isEnable) {
			((MainUIActivity)context).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			((MainUIActivity)context).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
}
