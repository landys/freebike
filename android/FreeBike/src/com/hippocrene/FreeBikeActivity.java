package com.hippocrene;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FreeBikeActivity extends MapActivity {
	private MapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        
        mMapView = (MapView)findViewById(R.id.map_view);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
            mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
        mMapView.getOverlays().add(mMyLocationOverlay);
        mMapView.getController().setZoom(10);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        
        Button btnList = (Button)findViewById(R.id.btn_list);
        btnList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 Intent intent = new Intent();
            	 intent.setClass(FreeBikeActivity.this, StopListActivity.class);
                 startActivity(intent);
                 FreeBikeActivity.this.finish();
            }
        });
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}