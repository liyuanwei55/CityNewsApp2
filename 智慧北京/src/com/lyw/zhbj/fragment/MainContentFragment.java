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
 *	��ҳ�������й����ǻ۷����������� ����ײ���ǩ����л�?
 *		viewpager�����view�໥�л�
 *	impl���е�������ṩ��Ӧ��view
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
		initFragmentData();//��ʼ��fragment����
	}
	/**
	 * ��fragment���������֣��ײ���RadioGroup�����ϲ���ViewPager
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
		
		//Ĭ����ҳ����
		mRadioGroup.check(R.id.rb_content_fragment_home); // ����Ĭ��ѡ�е���homeҳǩ
		pagerList.get(0).initPageData(); // ��ʼ����ҳ������
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
			System.out.println("Ԥ����: " + position);
			BasePage pager = pagerList.get(position);
			View rootView = pager.getRootView();//lyw ������ʵ������������ͼ�ķ��룬viewpage���ǻ�Ԥ������һҳ������ֻ�Ǹ��ռ��ӣ�û������
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
	 * RadioGroup�ļ����¼�
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
		
		mViewPager.setCurrentItem(index, false);//falseȥ������Ч��
		pagerList.get(index).initPageData(); // ��ʼ������
		
		if(index == 0 || index == 4) {
			// �˵�������
			//getSlidingMenu()   Gets the SlidingMenu associated with this activity.
			((MainUIActivity) activity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		} else {
			((MainUIActivity) activity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
	}
	
	/**
	 * ��ȡ��������ҳ��
	 * @return
	 */
	public NewsCenterPager getNewsCenterPager(){
		return (NewsCenterPager) pagerList.get(1);
	}
}
