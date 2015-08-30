package com.lyw.zhbj.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lyw.zhbj.R;

/**
 * 新闻列表的listview,为什么需要三个构造方法？
 * 在touch事件中启用/处理 下拉刷新操作
 * 在scroll事件中启用/处理 加载更多操作
 *
 */
public class RefreshListView extends ListView implements OnScrollListener {
	private LinearLayout rootHeaderView; // 整个头布局对象
	private View mPullDownHeaderView; // 下拉头布局的view对象
	private ImageView ivArrow; // 头布局的箭头
	private ProgressBar mProgressbar; // 头布局的进度圈
	private TextView tvState; // 头布局的状态
	private TextView tvLastUpdateTime; // 头布局的最后刷新时间
	private View mCustomHeaderView; // 在刷新头布局下添加的自定义头布局，即新闻轮播图
	private View mFooterView; // 脚布局对象

	private int mPullDownHeaderViewHeight; // 下拉头布局的高度
	private int mFooterViewHeight; // 脚布局的高度
	private int downY = -1; // 按下时y轴的偏移量
	//PULL_DOWN->RELEASE_REFRESH->REFRESHING->PULL_DOWN
	private final int PULL_DOWN = 0; // 下拉刷新
	private final int RELEASE_REFRESH = 1; // 释放刷新;
	private final int REFRESHING = 2; // 正在刷新中..
	private int currentState = PULL_DOWN; // 当前下拉头布局的状态, 默认为: 下拉刷新状态
	private boolean isLoadingMore = false; // 是否正在加载更多中, 默认为: false

	private RotateAnimation upAnim; // 向上旋转的动画
	private RotateAnimation downAnim; // 向下旋转的动画
	
	private OnRefreshListener mOnRefreshListener; // 下拉刷新和加载更多的回调接口
	
	private boolean isEnabledPullDownRefresh = false; // 是否启用下拉刷新
	private boolean isEnabledLoadingMore = false; // 是否启用加载更多

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initHeader();
		initFooter();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initHeader();
		initFooter();
	}

	public RefreshListView(Context context) {
		super(context);

		initHeader();
		initFooter();
	}
	
	/**
	 * 初始化脚布局
	 */
	private void initFooter() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_footer_view, null);
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		this.addFooterView(mFooterView);
		
		this.setOnScrollListener(this);
	}

	// 下拉刷新，头信息
	private void initHeader() {
		rootHeaderView = (LinearLayout) View.inflate(getContext(),
				R.layout.refresh_header_view, null);
		mPullDownHeaderView = rootHeaderView
				.findViewById(R.id.ll_refresh_header_view_pull_down);
		ivArrow = (ImageView) rootHeaderView
				.findViewById(R.id.iv_refresh_header_view_pull_down_arrow);
		mProgressbar = (ProgressBar) rootHeaderView
				.findViewById(R.id.pb_refresh_header_view_pull_down);
		tvState = (TextView) rootHeaderView
				.findViewById(R.id.tv_refresh_header_view_pull_down_state);
		tvLastUpdateTime = (TextView) rootHeaderView
				.findViewById(R.id.tv_refresh_header_view_pull_down_last_update_time);

		mPullDownHeaderView.measure(0, 0);
		mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
		System.out.println("头布局的高度: " + mPullDownHeaderViewHeight);

		rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//把头布局隐藏
		this.addHeaderView(rootHeaderView);

		// 初始化动画,即把动画准备好
		initAnimation();
	}

	private void initAnimation() {
		upAnim = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnim.setDuration(500);
		upAnim.setFillAfter(true);//true表示动画执行完毕后停留在那个状态

		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true);
	}

	/**
	 * 添加一个自定义的头布局.
	 * 
	 * @param v
	 */
	public void addCustomHeaderView(View v) {
		this.mCustomHeaderView = v;
		rootHeaderView.addView(v);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE://主要作用是把布局头拉出来
			// 如果没有启用下拉刷新功能, 直接跳出switch
			if(!isEnabledPullDownRefresh){
				break;
			}
			// 当前正在刷新中, 跳出switch
			if(currentState==REFRESHING){
				break;
			}
			// 判断添加的轮播图是否完全显示了, 如果没有完全显示, 
			// 不执行下面下拉头的代码, 跳转switch语句, 执行父元素的touch事件.
			if(mCustomHeaderView != null) {
				int[] location = new int[2];
				this.getLocationOnScreen(location);
				int listviewY = location[1];
				
				mCustomHeaderView.getLocationOnScreen(location);
				int customViewY = location[1];
				if(listviewY > customViewY){
					break;//跳出swich直接响应listview本来的touch事件
				}
			}
			
			//下面主要目的是把隐藏的头布局给拉出来
			if (downY == -1) {// 加上这句话效果会好很多
				downY = (int) ev.getY();
			}

			int moveY = (int) ev.getY();
			// 移动的差值
			int diffY = moveY - downY;
			System.out.println("diffY:" + diffY);
			/**
			 * 如果diffY差值大于0, 
			 * 向下拖拽 并且 当前ListView可见的第一个条目的索引等于0 
			 * 才进行下拉头的操作
			 */
			if (diffY > 0 && getFirstVisiblePosition() == 0) {
				int paddingTop = -mPullDownHeaderViewHeight + diffY;
				// System.out.println("paddingTop:"+paddingTop);

				if (paddingTop > 0 && currentState != RELEASE_REFRESH) {
					currentState = RELEASE_REFRESH;
					refreshPullDownState();
				} else if (paddingTop < 0 && currentState != PULL_DOWN) {
					currentState = PULL_DOWN;
					refreshPullDownState();
				}

				rootHeaderView.setPadding(0, paddingTop, 0, 0);//下拉头布局
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			/**
			 * 当前状态是下拉刷新状态, 把头布局隐藏.
			 * 当前状态是释放刷新, 把头布局完全显示, 并且进入到正在刷新中状态
			 */
			if(currentState == PULL_DOWN){
				rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//把头布局隐藏
			}else if(currentState == RELEASE_REFRESH){
				rootHeaderView.setPadding(0, 0, 0, 0);
				currentState = REFRESHING;
				refreshPullDownState();
				
				// 调用用户的回调接口,下拉刷新
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onRefresh();
				}
			}
			break;
		default:
			break;
		}

		return super.onTouchEvent(ev);// listview原本的touch事件相应方法
	}

	/**
	 * 根据currentState当前的状态, 来刷新头布局的状态
	 */
	private void refreshPullDownState() {
		switch (currentState) {
		case PULL_DOWN:
			ivArrow.startAnimation(downAnim);
			tvState.setText("下拉刷新");
			break;
		case RELEASE_REFRESH:
			ivArrow.startAnimation(upAnim);
			tvState.setText("释放刷新");
			break;
		case REFRESHING:
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
			tvState.setText("正在刷新中..");
			break;
		default:
			break;
		}
	}
	/**
	 * 设置刷新的监听事件
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener){//参考setOnPageChangeListener(OnPageChangeListener listener)
		this.mOnRefreshListener = listener;
	}
	/**
	 * 刷新的回调接口
	 * @author liyuanwei55
	 *
	 */
	public interface OnRefreshListener {
		/**
		 * 当下拉刷新时 触发此方法, 实现此方法是抓取数据.
		 */
		public void onRefresh();
		/**
		 * 当加载更多时, 触发此方法. 
		 */
		public void onLoadingMore();
		
	}
	/**
	 * 当数据刷新完成时调用此方法
	 */
	public void refreshFinish() {
		//把头布局隐藏起来，改变最后刷新时间，改变刷新状态
		rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//把头布局隐藏
		currentState = PULL_DOWN;
		mProgressbar.setVisibility(View.INVISIBLE);
		ivArrow.setVisibility(View.VISIBLE);
		tvState.setText("下拉刷新");
		tvLastUpdateTime.setText("最后刷新时间: " + getCurrentTime());
	}

	/**
	 * 获取当前时间, 格式为: 1990-09-09 09:09:09
	 * @return
	 */
	private String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	//以下是listview的滑动监听事件
	/**
	 * 当滚动的状态改变时触发此方法.
	 * scrollState 当前的状态
	 * 
	 * SCROLL_STATE_IDLE 停滞
	 * SCROLL_STATE_TOUCH_SCROLL 触摸滚动
	 * SCROLL_STATE_FLING 惯性滑动(猛的一滑)
	 * 
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当前没有启用加载更多的功能
		if(!isEnabledLoadingMore) {
			return;
		}
		
		if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			int lastVisiblePosition = getLastVisiblePosition();
			if((lastVisiblePosition==getCount() - 1) && isLoadingMore != true){//滑到了最底部了
				System.out.println("滑到最底部了");
				mFooterView.setPadding(0, 0, 0, 0);//显示出来
				// 把脚布局显示出来, 把ListView滑动到最低边
				this.setSelection(getCount());
				
				isLoadingMore = true;//避免重复加载数据
				
				// 调用用户的回调接口,加载更多数据
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}
	/**
	 * 当滚动时触发此方法
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 加载更多完毕后调用的方法
	 */
	public void loadMoreFinish(){
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);//隐藏
		isLoadingMore = false;//回到初始状态
	}
	
	/**
	 * 是否启用下拉刷新的功能
	 * @param isEnabled true 启用
	 */
	public void isEnabledPullDownRefresh(boolean isEnabled) {
		isEnabledPullDownRefresh = isEnabled;
	}

	/**
	 * 是否启用加载更多
	 * @param isEnabled
	 */
	public void isEnabledLoadingMore(boolean isEnabled) {
		isEnabledLoadingMore  = isEnabled;
	}
	
	
}
