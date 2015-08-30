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
	//true父类不拦截，自己相应事件，false父类拦截，自己不响应事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);// 请求父类不截取事件
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			
			int diffX = moveX - downX;
			int diffY = moveY - downY;
			//如果是横向滑动，父类不能拦截，如果是竖向滑动，父类可以拦截
			if(Math.abs(diffX) > Math.abs(diffY)){//横向滑动
				//如果是第一个页横向滑动左右，可以让父类处理，划出左侧菜单，如果是最后一页，可以让父类处理，改变viewpagerIndicator
				if(getCurrentItem()==0 && diffX>0){
					getParent().requestDisallowInterceptTouchEvent(false);
				}else if(getCurrentItem()==(getAdapter().getCount()-1) && diffX<0){
					getParent().requestDisallowInterceptTouchEvent(false);
				}else{
					getParent().requestDisallowInterceptTouchEvent(true);// 
				}
				
			}else{//竖向滑动
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			
			break;
		default:
			break;
		}
		

		return super.dispatchTouchEvent(ev);
	}
}
