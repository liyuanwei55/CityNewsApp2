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
 * �����б��listview,Ϊʲô��Ҫ�������췽����
 * ��touch�¼�������/���� ����ˢ�²���
 * ��scroll�¼�������/���� ���ظ������
 *
 */
public class RefreshListView extends ListView implements OnScrollListener {
	private LinearLayout rootHeaderView; // ����ͷ���ֶ���
	private View mPullDownHeaderView; // ����ͷ���ֵ�view����
	private ImageView ivArrow; // ͷ���ֵļ�ͷ
	private ProgressBar mProgressbar; // ͷ���ֵĽ���Ȧ
	private TextView tvState; // ͷ���ֵ�״̬
	private TextView tvLastUpdateTime; // ͷ���ֵ����ˢ��ʱ��
	private View mCustomHeaderView; // ��ˢ��ͷ��������ӵ��Զ���ͷ���֣��������ֲ�ͼ
	private View mFooterView; // �Ų��ֶ���

	private int mPullDownHeaderViewHeight; // ����ͷ���ֵĸ߶�
	private int mFooterViewHeight; // �Ų��ֵĸ߶�
	private int downY = -1; // ����ʱy���ƫ����
	//PULL_DOWN->RELEASE_REFRESH->REFRESHING->PULL_DOWN
	private final int PULL_DOWN = 0; // ����ˢ��
	private final int RELEASE_REFRESH = 1; // �ͷ�ˢ��;
	private final int REFRESHING = 2; // ����ˢ����..
	private int currentState = PULL_DOWN; // ��ǰ����ͷ���ֵ�״̬, Ĭ��Ϊ: ����ˢ��״̬
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ�����, Ĭ��Ϊ: false

	private RotateAnimation upAnim; // ������ת�Ķ���
	private RotateAnimation downAnim; // ������ת�Ķ���
	
	private OnRefreshListener mOnRefreshListener; // ����ˢ�ºͼ��ظ���Ļص��ӿ�
	
	private boolean isEnabledPullDownRefresh = false; // �Ƿ���������ˢ��
	private boolean isEnabledLoadingMore = false; // �Ƿ����ü��ظ���

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
	 * ��ʼ���Ų���
	 */
	private void initFooter() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_footer_view, null);
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		this.addFooterView(mFooterView);
		
		this.setOnScrollListener(this);
	}

	// ����ˢ�£�ͷ��Ϣ
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
		System.out.println("ͷ���ֵĸ߶�: " + mPullDownHeaderViewHeight);

		rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//��ͷ��������
		this.addHeaderView(rootHeaderView);

		// ��ʼ������,���Ѷ���׼����
		initAnimation();
	}

	private void initAnimation() {
		upAnim = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnim.setDuration(500);
		upAnim.setFillAfter(true);//true��ʾ����ִ����Ϻ�ͣ�����Ǹ�״̬

		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true);
	}

	/**
	 * ���һ���Զ����ͷ����.
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
		case MotionEvent.ACTION_MOVE://��Ҫ�����ǰѲ���ͷ������
			// ���û����������ˢ�¹���, ֱ������switch
			if(!isEnabledPullDownRefresh){
				break;
			}
			// ��ǰ����ˢ����, ����switch
			if(currentState==REFRESHING){
				break;
			}
			// �ж���ӵ��ֲ�ͼ�Ƿ���ȫ��ʾ��, ���û����ȫ��ʾ, 
			// ��ִ����������ͷ�Ĵ���, ��תswitch���, ִ�и�Ԫ�ص�touch�¼�.
			if(mCustomHeaderView != null) {
				int[] location = new int[2];
				this.getLocationOnScreen(location);
				int listviewY = location[1];
				
				mCustomHeaderView.getLocationOnScreen(location);
				int customViewY = location[1];
				if(listviewY > customViewY){
					break;//����swichֱ����Ӧlistview������touch�¼�
				}
			}
			
			//������ҪĿ���ǰ����ص�ͷ���ָ�������
			if (downY == -1) {// ������仰Ч����úܶ�
				downY = (int) ev.getY();
			}

			int moveY = (int) ev.getY();
			// �ƶ��Ĳ�ֵ
			int diffY = moveY - downY;
			System.out.println("diffY:" + diffY);
			/**
			 * ���diffY��ֵ����0, 
			 * ������ק ���� ��ǰListView�ɼ��ĵ�һ����Ŀ����������0 
			 * �Ž�������ͷ�Ĳ���
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

				rootHeaderView.setPadding(0, paddingTop, 0, 0);//����ͷ����
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			/**
			 * ��ǰ״̬������ˢ��״̬, ��ͷ��������.
			 * ��ǰ״̬���ͷ�ˢ��, ��ͷ������ȫ��ʾ, ���ҽ��뵽����ˢ����״̬
			 */
			if(currentState == PULL_DOWN){
				rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//��ͷ��������
			}else if(currentState == RELEASE_REFRESH){
				rootHeaderView.setPadding(0, 0, 0, 0);
				currentState = REFRESHING;
				refreshPullDownState();
				
				// �����û��Ļص��ӿ�,����ˢ��
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onRefresh();
				}
			}
			break;
		default:
			break;
		}

		return super.onTouchEvent(ev);// listviewԭ����touch�¼���Ӧ����
	}

	/**
	 * ����currentState��ǰ��״̬, ��ˢ��ͷ���ֵ�״̬
	 */
	private void refreshPullDownState() {
		switch (currentState) {
		case PULL_DOWN:
			ivArrow.startAnimation(downAnim);
			tvState.setText("����ˢ��");
			break;
		case RELEASE_REFRESH:
			ivArrow.startAnimation(upAnim);
			tvState.setText("�ͷ�ˢ��");
			break;
		case REFRESHING:
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ����..");
			break;
		default:
			break;
		}
	}
	/**
	 * ����ˢ�µļ����¼�
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener){//�ο�setOnPageChangeListener(OnPageChangeListener listener)
		this.mOnRefreshListener = listener;
	}
	/**
	 * ˢ�µĻص��ӿ�
	 * @author liyuanwei55
	 *
	 */
	public interface OnRefreshListener {
		/**
		 * ������ˢ��ʱ �����˷���, ʵ�ִ˷�����ץȡ����.
		 */
		public void onRefresh();
		/**
		 * �����ظ���ʱ, �����˷���. 
		 */
		public void onLoadingMore();
		
	}
	/**
	 * ������ˢ�����ʱ���ô˷���
	 */
	public void refreshFinish() {
		//��ͷ���������������ı����ˢ��ʱ�䣬�ı�ˢ��״̬
		rootHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);//��ͷ��������
		currentState = PULL_DOWN;
		mProgressbar.setVisibility(View.INVISIBLE);
		ivArrow.setVisibility(View.VISIBLE);
		tvState.setText("����ˢ��");
		tvLastUpdateTime.setText("���ˢ��ʱ��: " + getCurrentTime());
	}

	/**
	 * ��ȡ��ǰʱ��, ��ʽΪ: 1990-09-09 09:09:09
	 * @return
	 */
	private String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	//������listview�Ļ��������¼�
	/**
	 * ��������״̬�ı�ʱ�����˷���.
	 * scrollState ��ǰ��״̬
	 * 
	 * SCROLL_STATE_IDLE ͣ��
	 * SCROLL_STATE_TOUCH_SCROLL ��������
	 * SCROLL_STATE_FLING ���Ի���(�͵�һ��)
	 * 
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// ��ǰû�����ü��ظ���Ĺ���
		if(!isEnabledLoadingMore) {
			return;
		}
		
		if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			int lastVisiblePosition = getLastVisiblePosition();
			if((lastVisiblePosition==getCount() - 1) && isLoadingMore != true){//��������ײ���
				System.out.println("������ײ���");
				mFooterView.setPadding(0, 0, 0, 0);//��ʾ����
				// �ѽŲ�����ʾ����, ��ListView��������ͱ�
				this.setSelection(getCount());
				
				isLoadingMore = true;//�����ظ���������
				
				// �����û��Ļص��ӿ�,���ظ�������
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}
	/**
	 * ������ʱ�����˷���
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * ���ظ�����Ϻ���õķ���
	 */
	public void loadMoreFinish(){
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);//����
		isLoadingMore = false;//�ص���ʼ״̬
	}
	
	/**
	 * �Ƿ���������ˢ�µĹ���
	 * @param isEnabled true ����
	 */
	public void isEnabledPullDownRefresh(boolean isEnabled) {
		isEnabledPullDownRefresh = isEnabled;
	}

	/**
	 * �Ƿ����ü��ظ���
	 * @param isEnabled
	 */
	public void isEnabledLoadingMore(boolean isEnabled) {
		isEnabledLoadingMore  = isEnabled;
	}
	
	
}
