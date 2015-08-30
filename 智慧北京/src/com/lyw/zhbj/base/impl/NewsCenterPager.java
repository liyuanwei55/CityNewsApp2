package com.lyw.zhbj.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.lyw.zhbj.MainUIActivity;
import com.lyw.zhbj.base.BasePage;
import com.lyw.zhbj.base.MenuDetailBasePager;
import com.lyw.zhbj.base.newsimpl.InteractMenuDetailPager;
import com.lyw.zhbj.base.newsimpl.NewsCenterMenuDetailPager;
import com.lyw.zhbj.base.newsimpl.PhotosMenuDetailPager;
import com.lyw.zhbj.base.newsimpl.TopicMenuDetailPager;
import com.lyw.zhbj.domin.NewsCenterBean;
import com.lyw.zhbj.domin.NewsCenterBean.NewsCenterData;
import com.lyw.zhbj.fragment.LeftMenuFragment;
import com.lyw.zhbj.utils.ConstansPath;
import com.lyw.zhbj.utils.SharesPreferenceUntils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新闻中心的页面 左侧菜单是如何切换？新闻，专题，互动，组图。 FrameLayout.add(view)
 * newsimpl包中的死四个类把相应的view准备好
 */
public class NewsCenterPager extends BasePage {
	private List<MenuDetailBasePager> pagerList; // 左侧菜单对应的页面
	private List<NewsCenterData> menuDataList;// 左侧菜单数据
	private String result;// 请求网络返回的json字符串

	public NewsCenterPager(Context context) {
		super(context);
	}

	/**
	 * 该方法在MainContentFragment点击下方选项按钮时调用
	 */
	@Override
	public void initPageData() {
		System.out.println("新闻数据初始化了.");
		ib_menu.setVisibility(View.VISIBLE);
		tv_title.setText("新闻");

		String string = SharesPreferenceUntils.getString(context, ConstansPath.NEWSCENTER_URL, "");
		if(!TextUtils.isEmpty(string)){
			processData(string);
		}
		getDataFromNet();//请求网络数据前要能够缓存以前的数据

	}

	private void getDataFromNet() {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(ConstansPath.NEWSCENTER_URL,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						result = new String(responseBody);
						// System.out.println("请求成功：" + result);
						//把获取的数据存储到本地
						SharesPreferenceUntils.putString(context, ConstansPath.NEWSCENTER_URL, result);
						processData(result);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						Toast.makeText(context, "请求网络数据信息失败！", 0).show();
					}
				});

	}

	// 数据解析
	protected void processData(String result) {
		// Gson gson = new Gson();
		// java.lang.reflect.Type type = new TypeToken<JsonBean>() {}.getType();
		// JsonBean jsonBean = gson.fromJson(json, type);
		Gson gson = new Gson();
		NewsCenterBean bean = gson.fromJson(result, NewsCenterBean.class);
		
		// 在新闻中心tab选项中 准备左侧菜单的四个页面
		pagerList = new ArrayList<MenuDetailBasePager>();
		pagerList.add(new NewsCenterMenuDetailPager(context,bean.data.get(0).children));
		pagerList.add(new TopicMenuDetailPager(context));
		pagerList.add(new PhotosMenuDetailPager(context));
		pagerList.add(new InteractMenuDetailPager(context));

		// 把左侧菜单的数据传递给LeftMenuFragment
		menuDataList = bean.data;
		LeftMenuFragment leftMenuFragment = ((MainUIActivity) context)
				.getLeftMenuFragment();
		leftMenuFragment.setMenuDataList(menuDataList);//给左侧Menu菜单传递数据

		swichNewsMenuPager(0);// 默认显示第0个menu
	}

	public void swichNewsMenuPager(int position) {// 类中的独立方法
		final MenuDetailBasePager menuDetailBasePager = pagerList.get(position);
		View rootView = menuDetailBasePager.getRootView();
		fl_content.removeAllViews();
		fl_content.addView(rootView);
		// 标题跟着改变
		tv_title.setText(menuDataList.get(position).title);
		// 初始化数据
		menuDetailBasePager.initData();//分别调用子类的initData方法
		
		if(position == 2){
			ListOrGrid.setVisibility(View.VISIBLE);
			ListOrGrid.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PhotosMenuDetailPager photoPager = (PhotosMenuDetailPager) menuDetailBasePager;
					photoPager.swichListViewOrGridView(ListOrGrid);
				}
			});
			
		}else{
			ListOrGrid.setVisibility(View.GONE);
		}
		/**
		 * Activity中setContentView(view)是设置一个布局，即view的一个空架子，没有具体的内容，没数据
		 * 具体的数据设置是在Activity中具体设置的
		 * NewsCenterMenuDetailPager不是activity 但是提供view，但view的具体数据要靠initData()这个方法来实现
		 * 
		 */
	}

}
