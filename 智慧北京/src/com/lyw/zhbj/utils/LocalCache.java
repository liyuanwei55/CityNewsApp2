package com.lyw.zhbj.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class LocalCache {
	private static final File CACHEDIR = new File("/mnt/sdcard/itheima42");
	
	/**
	 * ����url, �򱾵ش洢һ��ͼƬ
	 * @param url
	 * @param bm
	 */
	public static void putBitmap(String url, Bitmap bm) {
		try {
			String fileName = MD5Encoder.encode(url).substring(0, 10);
			if(!CACHEDIR.exists()) {
				CACHEDIR.mkdir();
			}
			File cacheFile = new File(CACHEDIR, fileName);
			FileOutputStream fos = new FileOutputStream(cacheFile);
			bm.compress(CompressFormat.JPEG, 100, fos);//��bitmap��ѹ����ʽ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����url�ӱ��ػ�����ȡ��ͼƬ
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(String url) {
		try {
			String fileName = MD5Encoder.encode(url).substring(0, 10);
			File cacheFile = new File(CACHEDIR, fileName);
			if(cacheFile.exists()) {
				return BitmapFactory.decodeFile(cacheFile.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
