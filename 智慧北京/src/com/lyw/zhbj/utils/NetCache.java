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
	private MemoryCache mMemoryCache; // �ڴ滺�����

	public NetCache(Handler handler, MemoryCache mMemoryCache) {
		this.handler = handler;
		this.mMemoryCache = mMemoryCache;
		// ����һ���ڲ���5���̵߳��̳߳�
		newFixedThreadPool = Executors.newFixedThreadPool(5);
	}

	/**
	 * �������л�ȡͼƬ
	 * 
	 * @param url
	 * @param tag
	 */
	public void getBitmapFromNet(String url, int tag) {
		//new Thread(new InternalRunnable(url, tag)).start();
		newFixedThreadPool.execute(new InternalRunnable(url, tag));
	}

	class InternalRunnable implements Runnable {
		private String url; // ��ǰ������Ҫ����������ַ
		private int tag; // ��ǰ��������ͼƬ�ı�ʶ

		public InternalRunnable(String url, int tag) {
			this.url = url;
			this.tag = tag;
		}

		@Override
		public void run() {
			// ��������, ץȡͼƬ
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				int responseCode = conn.getResponseCode();
				if(responseCode == 200) {
					InputStream is = conn.getInputStream();
					// ����ת����ͼƬ
					Bitmap bm = BitmapFactory.decodeStream(is);
					
					Message msg = handler.obtainMessage();
					msg.obj = bm;
					msg.arg1 = tag;
					msg.what = SUCCESS;
					msg.sendToTarget();
					
					// �򱾵ش�һ��
					LocalCache.putBitmap(url, bm);
					// ���ڴ��һ��
					mMemoryCache.putBitmap(url, bm);
					
					return;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(conn != null) {
					conn.disconnect(); // �Ͽ�����
				}
			}
			handler.obtainMessage(FAILED).sendToTarget();
		}
		

	}
	
	
	
}
