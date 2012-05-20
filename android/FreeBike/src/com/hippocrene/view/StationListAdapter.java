package com.hippocrene.view;

import java.util.List;
import java.util.Map;

import com.hippocrene.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class StationListAdapter extends SimpleAdapter {

	public StationListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		if (position == 1) {
			view.setBackgroundResource(R.drawable.item_bg_selected);
		}
		else {
			view.setBackgroundResource(R.drawable.item_bg_normal);
		}
		
		return view;
	}

}
