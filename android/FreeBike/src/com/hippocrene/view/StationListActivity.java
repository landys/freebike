package com.hippocrene.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hippocrene.R;
import com.hippocrene.model.StationBikeInfo;
import com.hippocrene.service.WebRequestService;
import com.hippocrene.util.CommonUtil;

//http://www.itivy.com/android/archive/2011/7/18/android-listactivity-listview.html
public class StationListActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_list);
		ArrayList<HashMap<String, Object>> works = new ArrayList<HashMap<String, Object>>();

		WebRequestService webService = new WebRequestService(this);
		ArrayList<StationBikeInfo> bikeInfos = webService
				.getStationBikeInfosFromLocal(0);
		SimpleDateFormat sdf = CommonUtil.getSimpleDataFormat();
		for (int i = 0; i < bikeInfos.size() && i < 7; i++) {
			HashMap<String, Object> work = new HashMap<String, Object>();
			StationBikeInfo bikeInfo = bikeInfos.get(i);
			work.put("img", R.drawable.marker_red);
			work.put("name", bikeInfo.getStationId());
			work.put("time", "可借：" + bikeInfo.getAvCount() + "辆，可还：" + bikeInfo.getVctCount() + "辆");
			work.put("mark", "更新于：" + CommonUtil.longDateToString(sdf, bikeInfo.getUpdateTime()));
			works.add(work);
		}
		StationListAdapter simpleAdapter = new StationListAdapter(this,// Context
				works,// 数据源
				R.layout.station_item,// ListView中的每一项如何显示
				new String[] { "img", "name", "time", "mark" },// 对应数据源works中HashMap的每一个键值
				new int[] { R.id.img, R.id.workname, R.id.date, R.id.mark }// 对应View中的id
		);

		// ((ListView)findViewById(R.id.lv)).setAdapter(simpleAdapter);
		this.setListAdapter(simpleAdapter);

		Button btnList = (Button) findViewById(R.id.btn_map);
		btnList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(StationListActivity.this,
						FreeBikeActivity.class);
				startActivity(intent);
				// StationListActivity.this.finish();
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TextView txt = (TextView) this.findViewById(R.id.text);
		// txt.setText("あすは " + l.getSelectedItem().toString() + "です。");
	}
}
