package com.lyw.zhbj;

import java.util.ArrayList;
import java.util.List;

import com.lyw.zhbj.utils.SharesPreferenceUntils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class GuideActivity extends Activity implements OnPageChangeListener, OnClickListener {
	private ViewPager vp;
	private List<ImageView> list;
	private LinearLayout ll_guide_point_group;
	private View select_point;
	private int basicWidth;
	private Button btnStartExperience;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);
		
		vp = (ViewPager) findViewById(R.id.vp_guide);   
		ll_guide_point_group = (LinearLayout) findViewById(R.id.ll_guide_point_group);
		select_point = findViewById(R.id.select_point);//红点
		btnStartExperience = (Button) findViewById(R.id.bt_start_experience);
		btnStartExperience.setOnClickListener(this);//为button设置监听事件
		
		//oncreate无法获取控件的宽高信息，监听
		select_point.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				select_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				basicWidth = ll_guide_point_group.getChildAt(1).getLeft()-ll_guide_point_group.getChildAt(0).getLeft();
			}
		});
		initData();
		MyPageAdapt adapt = new MyPageAdapt();
		vp.setAdapter(adapt);
		vp.setOnPageChangeListener(this);//viewpage的监听事件
	}
	
	private void initData() {
		//准备一个viewpage用的list集合
		int[] imageResIDs = {
				R.drawable.guide_1,
				R.drawable.guide_2,
				R.drawable.guide_3
		};
		ImageView iv;
		list = new ArrayList<ImageView>();
		View view;
		LayoutParams p;
		//准备viewpage式也要把点准备出来
		for(int i=0;i<imageResIDs.length;i++){
			iv = new ImageView(this);
			iv.setBackgroundResource(imageResIDs[i]);
			list.add(iv);//图片数据准备完毕
			// 根据图片的个数, 每循环一次向LinearLayout中添加一个点
			view = new View(this);
			view.setBackgroundResource(R.drawable.point_normal);
			p = new LayoutParams(30,30);
			if(i!=0){
				p.leftMargin = 20;
			}
			view.setLayoutParams(p);
			ll_guide_point_group.addView(view);
		}
	}
	class MyPageAdapt extends PagerAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = list.get(position);
			container.addView(imageView);
			return imageView;
		}
		
	}
	//////////////////////////////////////////////
	/**
	 * 当页面正在滚动时
	 * position 当前选中的是哪个页面
	 * positionOffset 比例
	 * positionOffsetPixels 偏移像素
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		//int leftMargin = (int) (basicWidth*(position+positionOffset));
//		int leftMargin = 0;
//		if(positionOffset==1)
//			leftMargin = (int) (basicWidth*(position+positionOffset));
//		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) select_point.getLayoutParams();
//		layoutParams.leftMargin = leftMargin;
//		select_point.setLayoutParams(layoutParams);
	}

	/**
	 * 当页面被选中
	 */
	@Override
	public void onPageSelected(int position) {
		if(position == list.size() - 1) {
			btnStartExperience.setVisibility(View.VISIBLE);
		} else {
			btnStartExperience.setVisibility(View.GONE);
		}
		
		int leftMargin = (int) (basicWidth*(position));
		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) select_point.getLayoutParams();
		layoutParams.leftMargin = leftMargin;
		select_point.setLayoutParams(layoutParams);
	}
	/**
	 * 当页面滚动状态改变
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	/**
	 * button的监听事件
	 */
	@Override
	public void onClick(View arg0) {
		// 把IS_OPEN_MAIN_PAGER键, 在缓存中存储一个true.
		SharesPreferenceUntils.putBoolean(this,MainActivity.IS_MAINUI_OPEN, true);
		
		Intent intent = new Intent(GuideActivity.this,MainUIActivity.class);
		startActivity(intent);
		
		finish();
	}
	
	
}
