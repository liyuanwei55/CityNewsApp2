package com.lyw.zhbj.fragment;

import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lyw.zhbj.MainUIActivity;
import com.lyw.zhbj.R;
import com.lyw.zhbj.base.impl.NewsCenterPager;
import com.lyw.zhbj.domin.NewsCenterBean.NewsCenterData;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LeftMenuFragment extends Fragment implements OnItemClickListener {
	private List<NewsCenterData> menuListData;
	private ListView lv_menulist;
	private int selctedPosition = 0;
	private MenuListAdapter adapter;//listview的适配
	private Activity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_menu_fragment,null);
		lv_menulist = (ListView) view.findViewById(R.id.lv_menulist);
		return view;
	}
	//该方法在NewsCenterPager中调用
	public void setMenuDataList(List<NewsCenterData> menuListData){
		this.menuListData = menuListData;
		
		selctedPosition = 0;
		adapter = new MenuListAdapter();
		lv_menulist.setAdapter(adapter);
		lv_menulist.setOnItemClickListener(this);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
	}
	
	class MenuListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return menuListData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = null;
			if(convertView==null){
				view = (TextView) View.inflate(getActivity(), R.layout.left_menu_item, null);
			}else{
				view = (TextView) convertView;
			}
			view.setText(menuListData.get(position).title);
			//改变listview条目颜色
			view.setEnabled(selctedPosition==position);//如果true就设置为enabled;//改变条目颜色
			
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		selctedPosition = position;
		adapter.notifyDataSetChanged();//这样每点击一个条目，都调用getView(); any View reflecting the data set should refresh itself.
		
		//把slidingmenu合起来
		SlidingMenu slidingMenu = ((MainUIActivity)activity).getSlidingMenu();
		slidingMenu.toggle();
		
		//单击左侧菜单的时候切换页面，LeftMenuFragment中调用NewsCenterPager中的swichNewsMenuPager()方法
		NewsCenterPager newsCenterPager = ((MainUIActivity)activity).getMainContentFragment().getNewsCenterPager();
		newsCenterPager.swichNewsMenuPager(position);

	}
}
