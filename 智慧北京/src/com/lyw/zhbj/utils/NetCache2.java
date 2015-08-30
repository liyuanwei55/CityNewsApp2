package com.lyw.zhbj.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class NetCache2 {
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	private Handler handler;
	private Bitmap bm = null;

	public NetCache2(Handler handler) {
		this.handler = handler;
		
	}

	/**
	 * 从网络中获取图片
	 * 
	 * @param url
	 * @param tag
	 */
	public void getBitmapFromNet(String url, final int tag) {

		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				/*File sdCardDir = new File("/mnt/sdcard");
				File saveFile = new File(sdCardDir, "Bitmap"); 
				try {
					FileOutputStream fstream = new FileOutputStream(saveFile);
					fstream.write(responseBody);
				} catch (Exception e) {
					e.printStackTrace();
				}    */
				
				bm = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
				Message msg = handler.obtainMessage();
				msg.obj = bm;
				msg.arg1 = tag;
				msg.what = SUCCESS;
				msg.sendToTarget();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				
			}
		});
	
		
	}

	
}
