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
	private MenuListAdapter adapter;//listview������
	private Activity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_menu_fragment,null);
		lv_menulist = (ListView) view.findViewById(R.id.lv_menulist);
		return view;
	}
	//�÷�����NewsCenterPager�е���
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
			//�ı�listview��Ŀ��ɫ
			view.setEnabled(selctedPosition==position);//���true������Ϊenabled;//�ı���Ŀ��ɫ
			
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		selctedPosition = position;
		adapter.notifyDataSetChanged();//����ÿ���һ����Ŀ��������getView(); any View reflecting the data set should refresh itself.
		
		//��slidingmenu������
		SlidingMenu slidingMenu = ((MainUIActivity)activity).getSlidingMenu();
		slidingMenu.toggle();
		
		//�������˵���ʱ���л�ҳ�棬LeftMenuFragment�е���NewsCenterPager�е�swichNewsMenuPager()����
		NewsCenterPager newsCenterPager = ((MainUIActivity)activity).getMainContentFragment().getNewsCenterPager();
		newsCenterPager.swichNewsMenuPager(position);

	}
}
