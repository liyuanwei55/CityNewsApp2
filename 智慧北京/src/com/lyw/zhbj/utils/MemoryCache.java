package com.lyw.zhbj.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache {
	public LruCache<String, Bitmap> mMemoryCache;

	public MemoryCache() {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);

		// 定义的缓存大小为 运行时内存的八分之一
		mMemoryCache = new LruCache<String, Bitmap>(maxMemory) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				return value.getByteCount();
			}
		};
	}
	
	public void putBitmap(String url, Bitmap bm) {
		mMemoryCache.put(url, bm);
	}
	
	public Bitmap getBitmap(String url) {
		return mMemoryCache.get(url);
	}
}
