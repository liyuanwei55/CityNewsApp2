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
		select_point = findViewById(R.id.select_point);//���
		btnStartExperience = (Button) findViewById(R.id.bt_start_experience);
		btnStartExperience.setOnClickListener(this);//Ϊbutton���ü����¼�
		
		//oncreate�޷���ȡ�ؼ��Ŀ����Ϣ������
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
		vp.setOnPageChangeListener(this);//viewpage�ļ����¼�
	}
	
	private void initData() {
		//׼��һ��viewpage�õ�list����
		int[] imageResIDs = {
				R.drawable.guide_1,
				R.drawable.guide_2,
				R.drawable.guide_3
		};
		ImageView iv;
		list = new ArrayList<ImageView>();
		View view;
		LayoutParams p;
		//׼��viewpageʽҲҪ�ѵ�׼������
		for(int i=0;i<imageResIDs.length;i++){
			iv = new ImageView(this);
			iv.setBackgroundResource(imageResIDs[i]);
			list.add(iv);//ͼƬ����׼�����
			// ����ͼƬ�ĸ���, ÿѭ��һ����LinearLayout�����һ����
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
	 * ��ҳ�����ڹ���ʱ
	 * position ��ǰѡ�е����ĸ�ҳ��
	 * positionOffset ����
	 * positionOffsetPixels ƫ������
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
	 * ��ҳ�汻ѡ��
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
	 * ��ҳ�����״̬�ı�
	 */
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	/**
	 * button�ļ����¼�
	 */
	@Override
	public void onClick(View arg0) {
		// ��IS_OPEN_MAIN_PAGER��, �ڻ����д洢һ��true.
		SharesPreferenceUntils.putBoolean(this,MainActivity.IS_MAINUI_OPEN, true);
		
		Intent intent = new Intent(GuideActivity.this,MainUIActivity.class);
		startActivity(intent);
		
		finish();
	}
	
	
}
