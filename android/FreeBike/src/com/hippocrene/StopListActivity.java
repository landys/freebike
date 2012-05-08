package com.hippocrene;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

//http://www.itivy.com/android/archive/2011/7/18/android-listactivity-listview.html
public class StopListActivity extends ListActivity {
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.stoplist);

		List<String> items = fillArray();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_row, items);

		this.setListAdapter(adapter);
		
		Button btnList = (Button)findViewById(R.id.btn_map);
        btnList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 Intent intent = new Intent();
            	 intent.setClass(StopListActivity.this, FreeBikeActivity.class);
                 startActivity(intent);
                 StopListActivity.this.finish();
            }
        });
	}

	private List<String> fillArray() {
		List<String> items = new ArrayList<String>();
		items.add("a");
		items.add("b");
		items.add("c");
		items.add("d");
		items.add("e");
		items.add("f");
		items.add("g");
		return items;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TextView txt = (TextView) this.findViewById(R.id.text);
		// txt.setText("あすは " + l.getSelectedItem().toString() + "です。");
	}
}
