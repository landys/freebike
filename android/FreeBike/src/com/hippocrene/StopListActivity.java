package com.hippocrene;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//http://www.itivy.com/android/archive/2011/7/18/android-listactivity-listview.html
public class StopListActivity extends ListActivity {
	static final String[] COUNTRIES = new String[] {
	    "1", "2", "3", "4", "5",
	    "6", "7", "8", "9", "10",
	    "11", "12", "13", "14", "15",
	    "16", "17", "18", "19", "20",
	    "21", "22", "23", "24"
	  };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        Button btnMap = (Button)findViewById(R.id.btn_map2);
//        btnMap.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            	 Intent intent = new Intent();
//            	 intent.setClass(StopListActivity.this, FreeBikeActivity.class);
//                 startActivity(intent);
//                 StopListActivity.this.finish();
//            }
//        });
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.stoplist, COUNTRIES));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });
    }
}
