package com.hippocrene;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

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
        
        MapController mMapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (120.120029 * 1e6),
                (int) (30.281772 * 1e6));
        mMapController.setCenter(point);
        mMapController.setZoom(10);
        GeoPoint myGeoPoint = getCurrentGeoPoint();
        if (myGeoPoint != null) {
        	mMapController.animateTo(myGeoPoint);
        }
        
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
    
    private GeoPoint getCurrentGeoPoint() { 
    	Location location = null;
    	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
    		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	}
    	else if (locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) { 
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
    	}
    	else {
    		return null;
    	}
    	
        return new GeoPoint((int) (location.getLatitude() * 1e6), 
                (int) (location.getLongitude() * 1e6)); 
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}