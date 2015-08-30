package com.lyw.zhbj.domin;

import java.util.List;

public class NewsCenterBean {

	public int retcode;
	public List<NewsCenterData> data;
	public List<String> extend;
	
	public class NewsCenterData {
		
		public List<ChildRen> children;
		public int id;
		public String title;
		public int type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;
	}
	
	public class ChildRen {
		
		public int id;
		public int type;
		public String title;
		public String url;
	}
}
