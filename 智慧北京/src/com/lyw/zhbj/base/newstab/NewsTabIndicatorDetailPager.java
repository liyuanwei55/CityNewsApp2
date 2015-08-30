package com.lyw.zhbj.base.newstab;

import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.lyw.zhbj.NewsDetailUI;
import com.lyw.zhbj.R;
import com.lyw.zhbj.domin.NewsCenterBean.ChildRen;
import com.lyw.zhbj.domin.TabDetailBean;
import com.lyw.zhbj.domin.TabDetailBean.News;
import com.lyw.zhbj.domin.TabDetailBean.TopNew;
import com.lyw.zhbj.utils.ConstansPath;
import com.lyw.zhbj.utils.SharesPreferenceUntils;
import com.lyw.zhbj.view.HorizontalScrollViewPager;
import com.lyw.zhbj.view.RefreshListView;
import com.lyw.zhbj.view.RefreshListView.OnRefreshListener;

/**
 * viewPagerIndictor�л���Ӧ��view��
 *
 */
public class NewsTabIndicatorDetailPager implements OnPageChangeListener,
		OnTouchListener, OnRefreshListener, OnItemClickListener {
	private Context context;
	private ChildRen mChildRen; // ��ǰҳǩ����ҳ�������.
	private HorizontalScrollViewPager mViewPager;
	private TextView tvDescription;
	private LinearLayout llPointGroup;// �ֲ�ͼ���·���ԭ��
	private RefreshListView mListView;
	private String url; // ��ǰҳ���url

	private List<TopNew> topNewList; // �����ֲ�ͼ���ŵ�����

	private BitmapUtils bitmapUtils; // ͼƬ���ʿ��

	private int previousEnabledPosition; // ǰһ��ѡ�е������

	private List<News> newsList; // �б����ŵ�����
	
	private AsyncHttpClient asyncHttpClient;// ����������������
	private TopNewAdapter topNewAdapter;// �����ֲ�ͼviewpager��Adapter
	private NewsAdapter newsAdapter;// �����б�listview��adapter

	private String moreUrl; // �������ݵ�url
	// ����������id����
	private final String READ_NEWS_ID_ARRAY_KEY = "read_news_id_array";
	
	private Handler handler = new Handler() {// �����ֲ�ͼhandler
		public void handleMessage(android.os.Message msg) {
			int item = mViewPager.getCurrentItem() + 1;
			mViewPager.setCurrentItem(item % topNewList.size(), false);

			handler.postDelayed(new DelayRunnable(), 4000);
		};
	};

	public NewsTabIndicatorDetailPager(Context context, ChildRen mChildRen) {
		this.context = context;
		this.mChildRen = mChildRen;

		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}

	public View initView() {
		// listview����ͼ
		View listview = View.inflate(context, R.layout.news_menu_tab_detail,
				null);
		mListView = (RefreshListView) listview
				.findViewById(R.id.lv_news_menu_tab_detail_list_news);

		// listview�Ķ�����ͼ
		View topnews = View.inflate(context,
				R.layout.news_menu_tab_detail_topnews, null);
		mViewPager = (HorizontalScrollViewPager) topnews
				.findViewById(R.id.hsvp_news_menu_tab_detail_top_news);
		tvDescription = (TextView) topnews
				.findViewById(R.id.tv_news_menu_tab_detail_description);
		llPointGroup = (LinearLayout) topnews
				.findViewById(R.id.ll_news_menu_tab_detail_point_group);

		// mListView.addHeaderView(topnews);//listview���ͷ
		mListView.addCustomHeaderView(topnews);// RefreshListView�Զ��巽��
		mListView.setOnRefreshListener(this);
		mListView.isEnabledLoadingMore(true);
		mListView.isEnabledPullDownRefresh(true);
		mListView.setOnItemClickListener(this);

		return listview;
	}

	public void initData() {
		url = ConstansPath.SERVICE_URL + mChildRen.url;
		// ȡ�����������
		String json = SharesPreferenceUntils.getString(context, url, null);
		if (!TextUtils.isEmpty(json)) {
			processData(json);
		}

		getDataFromNet();
	}

	private void getDataFromNet() {

		asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				// System.out.println("����ɹ���" + result);
				// �ѻ�ȡ�����ݴ洢������
				SharesPreferenceUntils.putString(context, url, result);// ��Ϊurl
				processData(result);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(context, "��������������Ϣʧ�ܣ�", 0).show();
			}
		});
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		TabDetailBean bean = gson.fromJson(result, TabDetailBean.class);
		// ��ʼ���������ŵ�����
		topNewList = bean.data.topnews;

		moreUrl = bean.data.more;//���ظ�������
		if(!TextUtils.isEmpty(moreUrl)){//�ַ���Ϊ�շ����֣�null����""
			moreUrl =ConstansPath.SERVICE_URL+ moreUrl;
		}

		if (topNewAdapter == null) {
			topNewAdapter = new TopNewAdapter();
			mViewPager.setAdapter(topNewAdapter);
			mViewPager.setOnPageChangeListener(this);
			mViewPager.setOnTouchListener(this);// �ֲ�ͼƬ�Ĵ����¼�����ס���л�
		} else {
			topNewAdapter.notifyDataSetChanged();
		}

		// ��ʼ��ͼƬ�������͵�
		llPointGroup.removeAllViews();
		for (int i = 0; i < topNewList.size(); i++) {
			View view = new View(context);
			view.setBackgroundResource(R.drawable.tab_detail_top_news_point_bg);
			LayoutParams params = new LayoutParams(5, 5);
			if (i != 0) {
				params.leftMargin = 10;
			}
			view.setLayoutParams(params);
			view.setEnabled(false);
			llPointGroup.addView(view);
		}
		previousEnabledPosition = 0;
		tvDescription.setText(topNewList.get(previousEnabledPosition).title);
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(true);
		// ��̬�����ֲ�ͼ�л�����.
		/**
		 * -> 1.ʹ��handlerִ��һ����ʱ����: postDelayed -> 2.������runnable��run�����ᱻִ�� ->
		 * 3.ʹ��handler����һ����Ϣ -> 4.Handler���handleMessage�������յ���Ϣ. ->
		 * 5.��handleMessage������, ��ViewPager��ҳ���л�����һ��, ͬʱ:1.ʹ��handlerִ��һ����ʱ����
		 */

		handler.removeCallbacksAndMessages(null);// If token is null, all
													// callbacks and messages
													// will be removed.

		handler.postDelayed(new DelayRunnable(), 4000);

		// ��ʼ���б����ŵ�����
		newsList = bean.data.news;// ��ΪҪ�Ȼ�ȡ�����ϵ����ݣ����Էŵ�����

		if (newsAdapter == null) {
			newsAdapter = new NewsAdapter();
			mListView.setAdapter(newsAdapter);
		} else {
			newsAdapter.notifyDataSetChanged();
		}

	}

	class NewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return newsList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewsViewHolder mHolder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.news_menu_tab_detail_news_item, null);
				mHolder = new NewsViewHolder();
				mHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.iv_news_menu_tab_detail_news_item_image);
				mHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_news_menu_tab_detail_news_item_title);
				mHolder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_news_menu_tab_detail_news_item_date);
				convertView.setTag(mHolder);
			} else {
				mHolder = (NewsViewHolder) convertView.getTag();
			}

			News news = newsList.get(position);
			mHolder.tvTitle.setText(news.title);
			mHolder.tvDate.setText(news.pubdate);

			bitmapUtils.display(mHolder.ivImage, news.listimage);
			//���������ű�ɻ�ɫ
			String string = SharesPreferenceUntils.getString(context, READ_NEWS_ID_ARRAY_KEY, null);
			if(!TextUtils.isEmpty(string)&&string.contains(news.id)){
				mHolder.tvTitle.setTextColor(Color.GRAY);
			}else{
				mHolder.tvTitle.setTextColor(Color.BLACK);
			}

			return convertView;
		}

		class NewsViewHolder {

			public ImageView ivImage;
			public TextView tvTitle;
			public TextView tvDate;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class DelayRunnable implements Runnable {

		@Override
		public void run() {
			handler.sendEmptyMessage(0);
			// handler.obtainMessage().sendToTarget();
		}
	}

	class TopNewAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return topNewList.size();
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
			ImageView iv = new ImageView(context);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(R.drawable.home_scroll_default);

			String topImageUrl = topNewList.get(position).topimage;
			/**
			 * ��һ�������� ��ͼƬ��Ҫ��ʾ����һ���ؼ���: iv.setImageBitmap �ڶ��������� ͼƬ��url��ַ
			 */
			bitmapUtils.display(iv, topImageUrl);

			container.addView(iv);
			return iv;
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		tvDescription.setText(topNewList.get(position).title);
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(false);
		llPointGroup.getChildAt(position).setEnabled(true);
		previousEnabledPosition = position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	// �ֲ�ͼƬ�Ĵ����¼�����ס���л�
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("ֹͣ����");
			handler.removeCallbacksAndMessages(null);
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("��ʼ����");
			handler.postDelayed(new DelayRunnable(), 4000);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * listviewͷ������ˢ�¼����¼�
	 */
	@Override
	public void onRefresh() {
		// �ٴδ�ԭurl����������
		asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				mListView.refreshFinish();// ˢ�����

				String result = new String(responseBody);
				// System.out.println("����ɹ���" + result);
				// �ѻ�ȡ�����ݴ洢������
				SharesPreferenceUntils.putString(context, url, result);// ��Ϊurl
				processData(result);
				Toast.makeText(context, "����ˢ�����ݳɹ���", 0).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				mListView.refreshFinish();// ˢ�����
				Toast.makeText(context, "����ˢ������ʧ�ܣ�", 0).show();
			}
		});

	}

	/**
	 * ���ظ������ݻص�����
	 */
	@Override
	public void onLoadingMore() {
		// �鿴�Ƿ��и�������
		if (TextUtils.isEmpty(moreUrl)) {
			mListView.loadMoreFinish(); // �ѽŲ�������
			Toast.makeText(context, "û�и���������", 0).show();
		} else {
			// ���ظ�������
			asyncHttpClient = new AsyncHttpClient();
			asyncHttpClient.get(moreUrl, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					mListView.loadMoreFinish(); // �ѽŲ�������
					String result = new String(responseBody);
					Gson gson = new Gson();
					TabDetailBean bean = gson.fromJson(result, TabDetailBean.class);
					moreUrl = bean.data.more;
					if(!TextUtils.isEmpty(moreUrl)){
						moreUrl =ConstansPath.SERVICE_URL+ moreUrl;
					}
					
					List<News> newMoreList = bean.data.news;
					newsList.addAll(newMoreList);
					
					newsAdapter.notifyDataSetChanged();
					
					Toast.makeText(context, "���ظ������ݳɹ���", 0).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					mListView.loadMoreFinish(); // �ѽŲ�������
					Toast.makeText(context, "���ظ�������ʧ�ܣ�", 0).show();
				}
			});
		}

	}
	//�����б����Ŀ����¼�
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//1.�ĸ���Ŀ������ˣ���id�������� 2.�ѵ������url���ݸ���������ҳ���Activity
		News news = newsList.get(position -1);
		
		String readNewsIDArray = SharesPreferenceUntils.getString(null, READ_NEWS_ID_ARRAY_KEY, null);
		String idArray = null;
		if(TextUtils.isEmpty(readNewsIDArray)){
			idArray = news.id;
		}else{
			idArray = readNewsIDArray + "#" + news.id;
		}
		SharesPreferenceUntils.putString(context, READ_NEWS_ID_ARRAY_KEY, idArray);
		
		newsAdapter.notifyDataSetChanged();//�����µ���getCount��getView����
		
		Intent intent = new Intent(context, NewsDetailUI.class);
		intent.putExtra("url", news.url);
		context.startActivity(intent);
		
	}
	

}
