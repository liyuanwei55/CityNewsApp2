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
 * �������ĵ�ҳ�� ���˵�������л������ţ�ר�⣬��������ͼ�� FrameLayout.add(view)
 * newsimpl���е����ĸ������Ӧ��view׼����
 */
public class NewsCenterPager extends BasePage {
	private List<MenuDetailBasePager> pagerList; // ���˵���Ӧ��ҳ��
	private List<NewsCenterData> menuDataList;// ���˵�����
	private String result;// �������緵�ص�json�ַ���

	public NewsCenterPager(Context context) {
		super(context);
	}

	/**
	 * �÷�����MainContentFragment����·�ѡ�ťʱ����
	 */
	@Override
	public void initPageData() {
		System.out.println("�������ݳ�ʼ����.");
		ib_menu.setVisibility(View.VISIBLE);
		tv_title.setText("����");

		String string = SharesPreferenceUntils.getString(context, ConstansPath.NEWSCENTER_URL, "");
		if(!TextUtils.isEmpty(string)){
			processData(string);
		}
		getDataFromNet();//������������ǰҪ�ܹ�������ǰ������

	}

	private void getDataFromNet() {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(ConstansPath.NEWSCENTER_URL,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						result = new String(responseBody);
						// System.out.println("����ɹ���" + result);
						//�ѻ�ȡ�����ݴ洢������
						SharesPreferenceUntils.putString(context, ConstansPath.NEWSCENTER_URL, result);
						processData(result);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						Toast.makeText(context, "��������������Ϣʧ�ܣ�", 0).show();
					}
				});

	}

	// ���ݽ���
	protected void processData(String result) {
		// Gson gson = new Gson();
		// java.lang.reflect.Type type = new TypeToken<JsonBean>() {}.getType();
		// JsonBean jsonBean = gson.fromJson(json, type);
		Gson gson = new Gson();
		NewsCenterBean bean = gson.fromJson(result, NewsCenterBean.class);
		
		// ����������tabѡ���� ׼�����˵����ĸ�ҳ��
		pagerList = new ArrayList<MenuDetailBasePager>();
		pagerList.add(new NewsCenterMenuDetailPager(context,bean.data.get(0).children));
		pagerList.add(new TopicMenuDetailPager(context));
		pagerList.add(new PhotosMenuDetailPager(context));
		pagerList.add(new InteractMenuDetailPager(context));

		// �����˵������ݴ��ݸ�LeftMenuFragment
		menuDataList = bean.data;
		LeftMenuFragment leftMenuFragment = ((MainUIActivity) context)
				.getLeftMenuFragment();
		leftMenuFragment.setMenuDataList(menuDataList);//�����Menu�˵���������

		swichNewsMenuPager(0);// Ĭ����ʾ��0��menu
	}

	public void swichNewsMenuPager(int position) {// ���еĶ�������
		final MenuDetailBasePager menuDetailBasePager = pagerList.get(position);
		View rootView = menuDetailBasePager.getRootView();
		fl_content.removeAllViews();
		fl_content.addView(rootView);
		// ������Ÿı�
		tv_title.setText(menuDataList.get(position).title);
		// ��ʼ������
		menuDetailBasePager.initData();//�ֱ���������initData����
		
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
		 * Activity��setContentView(view)������һ�����֣���view��һ���ռ��ӣ�û�о�������ݣ�û����
		 * �����������������Activity�о������õ�
		 * NewsCenterMenuDetailPager����activity �����ṩview����view�ľ�������Ҫ��initData()���������ʵ��
		 * 
		 */
	}

}
