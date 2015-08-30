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
	private Boolean IsListView = true;//组图默认是listview显示
	private ImageUtils imageUtils;
	
	private Handler handler = new Handler(){//处理三级缓存 网络传回的msg
		public void handleMessage(android.os.Message msg) {
			ImageView iv = null;
			switch (msg.what) {
				case NetCache.SUCCESS:
					Bitmap bm = (Bitmap) msg.obj; // 图片
					int tag = msg.arg1; // 当前抓取图片的那个ImageView的tag
					if(IsListView){
						iv = (ImageView) mListView.findViewWithTag(tag);
					}else{
						iv = (ImageView) mGridView.findViewWithTag(tag);//因为这点小问题，搞了一下午
					}
					
					if(iv != null){
						iv.setImageBitmap(bm);
					}
					
					break;
				case NetCache.FAILED:
					Toast.makeText(context, "抓取图片失败", 0).show();
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
				// System.out.println("请求成功：" + result);
				// 把获取的数据存储到本地
				SharesPreferenceUntils.putString(context, ConstansPath.PHOTOS_URL, result);// 因为url
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
		PhotosBean bean = gson.fromJson(result, PhotosBean.class);
		photosItemList = bean.data.news;//组图的数据
		
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
			//不用Xutils框架来显示图片，使用图像的三级缓存
			/*BitmapUtils bitmapUtils = new BitmapUtils(context);
			bitmapUtils.configDefaultBitmapConfig(Config.ARGB_8888);
			bitmapUtils.display(mHolder.ivImage, photosItem.listimage);
			
			System.out.println("渲染view的position: "+position);*/
			
			// 为了防止图片错乱, 给ivImage设置一张默认的图片
			mHolder.ivImage.setImageResource(R.drawable.pic_item_list_default);
			
			mHolder.ivImage.setTag(position);//用于辨别请求回来的图片交与那个imageview显示
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
	
	//转换listview视图与gridview视图
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
