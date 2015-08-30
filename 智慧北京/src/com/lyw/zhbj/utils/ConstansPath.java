package com.lyw.zhbj.utils;
/**
 * 常量类
 * @author liyuanwei55
 *
 */
public class ConstansPath {
	/**
	 * 服务器前缀地址
	 */
	public static final String SERVICE_URL = "http://172.16.9.166:8080/zhbj";
	//public static final String SERVICE_URL = "http://192.168.2.155:8080/zhbj";
	
	// 新闻中心地址
	public static String NEWSCENTER_URL = SERVICE_URL + "/categories.json";
	
	// 组图地址
	public static String PHOTOS_URL = SERVICE_URL + "/photos/photos_1.json";
}
