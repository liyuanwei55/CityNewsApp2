package com.lyw.zhbj.utils;

import android.graphics.Bitmap;
import android.os.Handler;

public class ImageUtils {
	private NetCache netCache;
	private MemoryCache mMemoryCache;

	public ImageUtils(Handler handler) {
		// �����ڴ滺�����
		mMemoryCache = new MemoryCache();
		//�������绺�����.
		netCache = new NetCache(handler,mMemoryCache);
	}

	/**
	 * ����url��ȡͼƬ
	 * @param url
	 * @param tag
	 * @return
	 */
	public Bitmap getImageFromUrl(String url, int tag) {
		Bitmap bm = null;
		// 1. ȥ�ڴ���ȡ, ȡ����֮��ֱ�ӷ���.
		bm = mMemoryCache.getBitmap(url);
		if(bm != null) {
			System.out.println("���ڴ���ȡ");
			return bm;
		}
		// 2. ȥ������ȡ, ȡ����֮��ֱ�ӷ���.
		bm = LocalCache.getBitmap(url);
		if(bm != null) {
			System.out.println("�ӱ�����ȡ");
			return bm;
		}
		// 3. ȥ������ȡ, �������߳��첽ץȡ, ����ֱ�ӷ���. ��ץȡ��Ϻ�, �õ�ͼƬ, ʹ��handler������Ϣ����������.
		//��ֱ�ӷ�������Ϊ
		System.out.println("��������ȡ:"+tag);
		netCache.getBitmapFromNet(url, tag);
		
		return null;
	}
}
