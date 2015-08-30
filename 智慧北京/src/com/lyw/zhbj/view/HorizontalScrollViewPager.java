package com.lyw.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalScrollViewPager extends ViewPager {
	private int downX;
	private int downY;


	public HorizontalScrollViewPager(Context context) {
		super(context);
	}

	// Custom view HorizontalScrollViewPager is not using the 2- or 3-argument
	// View constructors; XML attributes will not work
	public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//true���಻���أ��Լ���Ӧ�¼���false�������أ��Լ�����Ӧ�¼�
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);// �����಻��ȡ�¼�
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			
			int diffX = moveX - downX;
			int diffY = moveY - downY;
			//����Ǻ��򻬶������಻�����أ���������򻬶��������������
			if(Math.abs(diffX) > Math.abs(diffY)){//���򻬶�
				//����ǵ�һ��ҳ���򻬶����ң������ø��ദ���������˵�����������һҳ�������ø��ദ���ı�viewpagerIndicator
				if(getCurrentItem()==0 && diffX>0){
					getParent().requestDisallowInterceptTouchEvent(false);
				}else if(getCurrentItem()==(getAdapter().getCount()-1) && diffX<0){
					getParent().requestDisallowInterceptTouchEvent(false);
				}else{
					getParent().requestDisallowInterceptTouchEvent(true);// 
				}
				
			}else{//���򻬶�
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			
			break;
		default:
			break;
		}
		

		return super.dispatchTouchEvent(ev);
	}
}
