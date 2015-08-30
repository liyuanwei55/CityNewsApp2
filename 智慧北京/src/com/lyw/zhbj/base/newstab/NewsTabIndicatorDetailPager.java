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
 * viewPagerIndictor切换对应的view类
 *
 */
public class NewsTabIndicatorDetailPager implements OnPageChangeListener,
		OnTouchListener, OnRefreshListener, OnItemClickListener {
	private Context context;
	private ChildRen mChildRen; // 当前页签详情页面的数据.
	private HorizontalScrollViewPager mViewPager;
	private TextView tvDescription;
	private LinearLayout llPointGroup;// 轮播图右下方的原点
	private RefreshListView mListView;
	private String url; // 当前页面的url

	private List<TopNew> topNewList; // 顶部轮播图新闻的数据

	private BitmapUtils bitmapUtils; // 图片访问框架

	private int previousEnabledPosition; // 前一个选中点的索引

	private List<News> newsList; // 列表新闻的数据
	
	private AsyncHttpClient asyncHttpClient;// 用于请求网络数据
	private TopNewAdapter topNewAdapter;// 顶部轮播图viewpager的Adapter
	private NewsAdapter newsAdapter;// 新闻列表listview的adapter

	private String moreUrl; // 更多数据的url
	// 读过的新闻id数组
	private final String READ_NEWS_ID_ARRAY_KEY = "read_news_id_array";
	
	private Handler handler = new Handler() {// 顶部轮播图handler
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
		// listview的视图
		View listview = View.inflate(context, R.layout.news_menu_tab_detail,
				null);
		mListView = (RefreshListView) listview
				.findViewById(R.id.lv_news_menu_tab_detail_list_news);

		// listview的顶部视图
		View topnews = View.inflate(context,
				R.layout.news_menu_tab_detail_topnews, null);
		mViewPager = (HorizontalScrollViewPager) topnews
				.findViewById(R.id.hsvp_news_menu_tab_detail_top_news);
		tvDescription = (TextView) topnews
				.findViewById(R.id.tv_news_menu_tab_detail_description);
		llPointGroup = (LinearLayout) topnews
				.findViewById(R.id.ll_news_menu_tab_detail_point_group);

		// mListView.addHeaderView(topnews);//listview添加头
		mListView.addCustomHeaderView(topnews);// RefreshListView自定义方法
		mListView.setOnRefreshListener(this);
		mListView.isEnabledLoadingMore(true);
		mListView.isEnabledPullDownRefresh(true);
		mListView.setOnItemClickListener(this);

		return listview;
	}

	public void initData() {
		url = ConstansPath.SERVICE_URL + mChildRen.url;
		// 取出缓存的数据
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
				// System.out.println("请求成功：" + result);
				// 把获取的数据存储到本地
				SharesPreferenceUntils.putString(context, url, result);// 因为url
				processData(result);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(context, "请求网络数据信息失败！", 0).show();
			}
		});
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		TabDetailBean bean = gson.fromJson(result, TabDetailBean.class);
		// 初始化顶部新闻的数据
		topNewList = bean.data.topnews;

		moreUrl = bean.data.more;//加载更多数据
		if(!TextUtils.isEmpty(moreUrl)){//字符串为空分两种，null或是""
			moreUrl =ConstansPath.SERVICE_URL+ moreUrl;
		}

		if (topNewAdapter == null) {
			topNewAdapter = new TopNewAdapter();
			mViewPager.setAdapter(topNewAdapter);
			mViewPager.setOnPageChangeListener(this);
			mViewPager.setOnTouchListener(this);// 轮播图片的触摸事件，按住不切换
		} else {
			topNewAdapter.notifyDataSetChanged();
		}

		// 初始化图片的描述和点
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
		// 动态的让轮播图切换起来.
		/**
		 * -> 1.使用handler执行一个延时任务: postDelayed -> 2.任务类runnable的run方法会被执行 ->
		 * 3.使用handler发送一个消息 -> 4.Handler类的handleMessage方法接收到消息. ->
		 * 5.在handleMessage方法中, 把ViewPager的页面切换到下一个, 同时:1.使用handler执行一个延时任务
		 */

		handler.removeCallbacksAndMessages(null);// If token is null, all
													// callbacks and messages
													// will be removed.

		handler.postDelayed(new DelayRunnable(), 4000);

		// 初始化列表新闻的数据
		newsList = bean.data.news;// 因为要先获取网络上的数据，所以放到这里

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
			//读过的新闻变成灰色
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
			 * 第一个参数是 把图片将要显示在哪一个控件上: iv.setImageBitmap 第二个参数是 图片的url地址
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

	// 轮播图片的触摸事件，按住不切换
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("停止播放");
			handler.removeCallbacksAndMessages(null);
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("开始播放");
			handler.postDelayed(new DelayRunnable(), 4000);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * listview头的下拉刷新监听事件
	 */
	@Override
	public void onRefresh() {
		// 再次从原url中请求数据
		asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				mListView.refreshFinish();// 刷新完毕

				String result = new String(responseBody);
				// System.out.println("请求成功：" + result);
				// 把获取的数据存储到本地
				SharesPreferenceUntils.putString(context, url, result);// 因为url
				processData(result);
				Toast.makeText(context, "下拉刷新数据成功！", 0).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				mListView.refreshFinish();// 刷新完毕
				Toast.makeText(context, "下拉刷新数据失败！", 0).show();
			}
		});

	}

	/**
	 * 加载更多数据回调方法
	 */
	@Override
	public void onLoadingMore() {
		// 查看是否还有更多数据
		if (TextUtils.isEmpty(moreUrl)) {
			mListView.loadMoreFinish(); // 把脚布局隐藏
			Toast.makeText(context, "没有更多数据了", 0).show();
		} else {
			// 加载更多数据
			asyncHttpClient = new AsyncHttpClient();
			asyncHttpClient.get(moreUrl, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					mListView.loadMoreFinish(); // 把脚布局隐藏
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
					
					Toast.makeText(context, "加载更多数据成功！", 0).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					mListView.loadMoreFinish(); // 把脚布局隐藏
					Toast.makeText(context, "加载更多数据失败！", 0).show();
				}
			});
		}

	}
	//新闻列表的条目点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//1.哪个条目被点击了，把id保存起来 2.把点击新闻url传递给新闻详情页面的Activity
		News news = newsList.get(position -1);
		
		String readNewsIDArray = SharesPreferenceUntils.getString(null, READ_NEWS_ID_ARRAY_KEY, null);
		String idArray = null;
		if(TextUtils.isEmpty(readNewsIDArray)){
			idArray = news.id;
		}else{
			idArray = readNewsIDArray + "#" + news.id;
		}
		SharesPreferenceUntils.putString(context, READ_NEWS_ID_ARRAY_KEY, idArray);
		
		newsAdapter.notifyDataSetChanged();//会重新调用getCount与getView方法
		
		Intent intent = new Intent(context, NewsDetailUI.class);
		intent.putExtra("url", news.url);
		context.startActivity(intent);
		
	}
	

}
