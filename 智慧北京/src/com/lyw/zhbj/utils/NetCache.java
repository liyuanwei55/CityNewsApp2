package com.lyw.zhbj.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NetCache {
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	private Handler handler;
	private ExecutorService newFixedThreadPool;
	private Bitmap bm = null;
	private MemoryCache mMemoryCache; // 内存缓存对象

	public NetCache(Handler handler, MemoryCache mMemoryCache) {
		this.handler = handler;
		this.mMemoryCache = mMemoryCache;
		// 构建一个内部有5个线程的线程池
		newFixedThreadPool = Executors.newFixedThreadPool(5);
	}

	/**
	 * 从网络中获取图片
	 * 
	 * @param url
	 * @param tag
	 */
	public void getBitmapFromNet(String url, int tag) {
		//new Thread(new InternalRunnable(url, tag)).start();
		newFixedThreadPool.execute(new InternalRunnable(url, tag));
	}

	class InternalRunnable implements Runnable {
		private String url; // 当前任务需要请求的网络地址
		private int tag; // 当前这次请求的图片的标识

		public InternalRunnable(String url, int tag) {
			this.url = url;
			this.tag = tag;
		}

		@Override
		public void run() {
			// 访问网络, 抓取图片
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				int responseCode = conn.getResponseCode();
				if(responseCode == 200) {
					InputStream is = conn.getInputStream();
					// 把流转换成图片
					Bitmap bm = BitmapFactory.decodeStream(is);
					
					Message msg = handler.obtainMessage();
					msg.obj = bm;
					msg.arg1 = tag;
					msg.what = SUCCESS;
					msg.sendToTarget();
					
					// 向本地存一份
					LocalCache.putBitmap(url, bm);
					// 向内存存一份
					mMemoryCache.putBitmap(url, bm);
					
					return;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(conn != null) {
					conn.disconnect(); // 断开连接
				}
			}
			handler.obtainMessage(FAILED).sendToTarget();
		}
		

	}
	
	
	
}
