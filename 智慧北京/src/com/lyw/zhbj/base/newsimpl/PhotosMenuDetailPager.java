package com.lyw.zhbj.base.newsimpl;

import java.util.List;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.lyw.zhbj.R;
import com.lyw.zhbj.base.MenuDetailBasePager;
import com.lyw.zhbj.domin.PhotosBean;
import com.lyw.zhbj.domin.PhotosBean.PhotosItem;
import com.lyw.zhbj.utils.ConstansPath;
import com.lyw.zhbj.utils.ImageUtils;
import com.lyw.zhbj.utils.NetCache;
import com.lyw.zhbj.utils.SharesPreferenceUntils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosMenuDetailPager extends MenuDetailBasePager{
	
	private ListView mListView;
	private GridView mGridView;
	private List<PhotosItem> photosItemList;
	private Boolean IsListView = true;//��ͼĬ����listview��ʾ
	private ImageUtils imageUtils;
	
	private Handler handler = new Handler(){//������������ ���紫�ص�msg
		public void handleMessage(android.os.Message msg) {
			ImageView iv = null;
			switch (msg.what) {
				case NetCache.SUCCESS:
					Bitmap bm = (Bitmap) msg.obj; // ͼƬ
					int tag = msg.arg1; // ��ǰץȡͼƬ���Ǹ�ImageView��tag
					if(IsListView){
						iv = (ImageView) mListView.findViewWithTag(tag);
					}else{
						iv = (ImageView) mGridView.findViewWithTag(tag);//��Ϊ���С���⣬����һ����
					}
					
					if(iv != null){
						iv.setImageBitmap(bm);
					}
					
					break;
				case NetCache.FAILED:
					Toast.makeText(context, "ץȡͼƬʧ��", 0).show();
					break;
				default:
					break;
			}
		}
	};

	public PhotosMenuDetailPager(Context context) {
		super(context);
	}

	public View initView() {
		View view = View.inflate(context, R.layout.photos, null);
		mListView = (ListView) view.findViewById(R.id.lv_photos);
		mGridView = (GridView) view.findViewById(R.id.gv_photos);
		return view;
	}
	
	@Override
	public void initData() {
		imageUtils = new ImageUtils(handler);
		
		String cacheStringData = SharesPreferenceUntils.getString(context, ConstansPath.PHOTOS_URL, "");
		if(!TextUtils.isEmpty(cacheStringData)){
			processData(cacheStringData);
		}
		
		getDataFromNet();
	}

	private void getDataFromNet() {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(ConstansPath.PHOTOS_URL, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				// System.out.println("����ɹ���" + result);
				// �ѻ�ȡ�����ݴ洢������
				SharesPreferenceUntils.putString(context, ConstansPath.PHOTOS_URL, result);// ��Ϊurl
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
		PhotosBean bean = gson.fromJson(result, PhotosBean.class);
		photosItemList = bean.data.news;//��ͼ������
		
		mListView.setAdapter(new PhotosAdapter());
		
	}
	
	class PhotosAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return photosItemList.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PhotosViewHolder mHolder = null;
			if(convertView == null){
				convertView = View.inflate(context, R.layout.photos_item, null);
				mHolder = new PhotosViewHolder();
				mHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_photos_item);
				mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_photos_item);
				convertView.setTag(mHolder);
			} else {
				mHolder = (PhotosViewHolder) convertView.getTag();
			}
			PhotosItem photosItem = photosItemList.get(position);
			mHolder.tvTitle.setText(photosItem.title);
			//����Xutils�������ʾͼƬ��ʹ��ͼ�����������
			/*BitmapUtils bitmapUtils = new BitmapUtils(context);
			bitmapUtils.configDefaultBitmapConfig(Config.ARGB_8888);
			bitmapUtils.display(mHolder.ivImage, photosItem.listimage);
			
			System.out.println("��Ⱦview��position: "+position);*/
			
			// Ϊ�˷�ֹͼƬ����, ��ivImage����һ��Ĭ�ϵ�ͼƬ
			mHolder.ivImage.setImageResource(R.drawable.pic_item_list_default);
			
			mHolder.ivImage.setTag(position);//���ڱ�����������ͼƬ�����Ǹ�imageview��ʾ
			Bitmap bitmap = imageUtils.getImageFromUrl(photosItem.listimage, position);
			if(bitmap != null){
				mHolder.ivImage.setImageBitmap(bitmap);
			}
			
			return convertView;
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
	
	class PhotosViewHolder {
		
		public ImageView ivImage;
		public TextView tvTitle;
	}
	
	//ת��listview��ͼ��gridview��ͼ
	public void swichListViewOrGridView(ImageButton listOrGrid) {
		if(IsListView){
			mListView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			listOrGrid.setImageResource(R.drawable.icon_pic_list_type);
			mGridView.setAdapter(new PhotosAdapter());
			IsListView = false;
		}else{
			mListView.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.GONE);
			listOrGrid.setImageResource(R.drawable.icon_pic_grid_type);
			mListView.setAdapter(new PhotosAdapter());
			IsListView = true;
		}
	}
	
}
