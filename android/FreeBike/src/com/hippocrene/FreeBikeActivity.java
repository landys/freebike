package com.hippocrene;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
//import com.mobclick.android.MobclickAgent;
import com.hippocrene.MyLocationManager.LocationCallBack;

public class FreeBikeActivity  extends MapActivity implements LocationCallBack, OnClickListener{
    /** Called when the activity is first created. */
	private final String TAG = "FreeBikeActivity";
	private MapView mapView;
	private MapController mMapCtrl;
	private View popView;
	private Drawable myLocationDrawable;
	private Drawable mylongPressDrawable;
	private MyLocationManager fzLocation;
	private MyItemizedOverlay myLocationOverlay;//我的位置 层
	private MyItemizedOverlay mLongPressOverlay; //长按时间层
	private List<Overlay> mapOverlays;
	private OverlayItem overlayitem = null;
	private String query;
	public GeoPoint locPoint;
	
	ImageButton loction_Btn;
	ImageButton layer_Btn;
	ImageButton pointwhat_Btn;
	Button search_btn;
	
	public final int MSG_VIEW_LONGPRESS = 10001;
	public final int MSG_VIEW_ADDRESSNAME = 10002;
	public final int MSG_VIEW_ADDRESSNAME_FAIL = 10004;
	public final int MSG_VIEW_LOCATIONLATLNG = 10003;
	public final int MSG_VIEW_LOCATIONLATLNG_FAIL = 10005;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        loction_Btn = (ImageButton)findViewById(R.id.loction);
    	layer_Btn = (ImageButton)findViewById(R.id.layer);
    	pointwhat_Btn = (ImageButton)findViewById(R.id.pointwhat);
    	loction_Btn = (ImageButton)findViewById(R.id.loction);
    	search_btn = (Button)findViewById(R.id.search);
    	
    	loction_Btn.setOnClickListener(this);
    	layer_Btn.setOnClickListener(this);
    	pointwhat_Btn.setOnClickListener(this);
    	search_btn.setOnClickListener(this);
    	
        myLocationDrawable = getResources().getDrawable(R.drawable.point_where);
        mylongPressDrawable = getResources().getDrawable(R.drawable.point_start);
        
        mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);
		initPopView();
		mMapCtrl = mapView.getController();
		myLocationOverlay = new MyItemizedOverlay(myLocationDrawable,this, mapView, popView, mMapCtrl);
		mLongPressOverlay = new MyItemizedOverlay(mylongPressDrawable,this, mapView, popView, mMapCtrl);
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(new LongPressOverlay(this, mapView, mHandler, mMapCtrl));
		//以北京市中心为中心
		GeoPoint cityLocPoint = new GeoPoint(39909230, 116397428);
		mMapCtrl.animateTo(cityLocPoint);
		mMapCtrl.setZoom(12);
		MyLocationManager.init(FreeBikeActivity.this.getApplicationContext() , FreeBikeActivity.this);
		fzLocation = MyLocationManager.getInstance();
		
    }
    
    
    private void initPopView(){
    	if(null == popView){
			popView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
			mapView.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView.setVisibility(View.GONE);
    	}
       
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCurrentLocation(Location location) {
		Log.d(TAG, "onCurrentLocationy");

		GeoPoint point = new GeoPoint(
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		overlayitem = new OverlayItem(point, "我的位置", "");
		mMapCtrl.setZoom(16);
		if(myLocationOverlay.size() > 0){
			myLocationOverlay.removeOverlay(0);
		}
		myLocationOverlay.addOverlay(overlayitem);
		mapOverlays.add(myLocationOverlay);
		mMapCtrl.animateTo(point);
	}
	
	

	/**
	 * 通过经纬度获取地址
	 * @param point
	 * @return
	 */
	private String getLocationAddress(GeoPoint point){
		String add = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(),
				Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
			Address address = addresses.get(0);
			int maxLine = address.getMaxAddressLineIndex();
			if(maxLine >= 2){
				add =  address.getAddressLine(1) + address.getAddressLine(2);
			}else {
				add = address.getAddressLine(1);
			}
		} catch (IOException e) {
			add = "";
			e.printStackTrace();
		}
		return add;
	}
	
	
	
	private Address searchLocationByName(String addressName){
		Geocoder geoCoder = new Geocoder(getBaseContext(),
				Locale.CHINA);
		try {
			List<Address> addresses = geoCoder.getFromLocationName(addressName, 1);
			Address address_send = null;
			for(Address address : addresses){
				locPoint = new GeoPoint((int)(address.getLatitude() * 1E6), (int)(address.getLongitude() * 1E6));
				address.getAddressLine(1);
				address_send = address;
			}
			return address_send;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_VIEW_LONGPRESS://处理长按时间返回位置信息
				{
					if(null == locPoint) return;
					new Thread( new Runnable() {
						@Override
						public void run() {
							String addressName = "";
							
							int count = 0;
							while(true){
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								count++;
								addressName = getLocationAddress(locPoint);
								Log.d(TAG, "获取地址名称");
								//请求五次获取不到结果就返回
								if("".equals(addressName) && count > 5){
									Message msg1 = new Message();
									msg1.what = MSG_VIEW_ADDRESSNAME_FAIL;
									mHandler.sendMessage(msg1);
									break;
								}else if("".equals(addressName) ){
									continue;
								}else{
									break;
								}
								
								
							}
							if(!"".equals(addressName) || count < 5){
								Message msg = new Message();
								msg.what = MSG_VIEW_ADDRESSNAME;
								msg.obj = addressName;
								mHandler.sendMessage(msg);
							}
						}
					}
					).start();
					overlayitem = new OverlayItem(locPoint, "地址名称",
							"正在地址加载...");
					if(mLongPressOverlay.size() > 0){
						mLongPressOverlay.removeOverlay(0);
					}
					popView.setVisibility(View.GONE);
					mLongPressOverlay.addOverlay(overlayitem);
					mLongPressOverlay.setFocus(overlayitem);
					mapOverlays.add(mLongPressOverlay);
					mMapCtrl.animateTo(locPoint);
					mapView.invalidate();
				}
				break;
			case MSG_VIEW_ADDRESSNAME:
				{
				//获取到地址后显示在泡泡上
					TextView desc = (TextView) popView.findViewById(R.id.map_bubbleText);
					desc.setText((String)msg.obj);
					popView.setVisibility(View.VISIBLE);
				}
				break;
			case MSG_VIEW_ADDRESSNAME_FAIL:
				{
					TextView desc = (TextView) popView.findViewById(R.id.map_bubbleText);
					desc.setText("获取地址失败");
					popView.setVisibility(View.VISIBLE);
				}
				break;
			case MSG_VIEW_LOCATIONLATLNG:
				{
					CommonHelper.closeProgress();
					Address address = (Address)msg.obj;
					locPoint = new GeoPoint((int)(address.getLatitude() * 1E6), (int)(address.getLongitude() * 1E6));
					overlayitem = new OverlayItem(locPoint, "地址名称",
							address.getAddressLine(1));
					if(mLongPressOverlay.size() > 0){
						mLongPressOverlay.removeOverlay(0);
					}
					popView.setVisibility(View.GONE);
					mLongPressOverlay.addOverlay(overlayitem);
					mLongPressOverlay.setFocus(overlayitem);
					mapOverlays.add(mLongPressOverlay);
					mMapCtrl.animateTo(locPoint);
					mapView.invalidate();
				}
				break;
			case MSG_VIEW_LOCATIONLATLNG_FAIL:
			{
				CommonHelper.closeProgress();
				Toast.makeText(FreeBikeActivity.this, "搜索地址失败", Toast.LENGTH_SHORT).show();
			}
				break;
			}
		}
	};
			
	
	//处理三个button的事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loction:
		{
			
		}
			break;
		case R.id.search:
		{
			onSearchRequested();
		}
			break;
		
		default:
			break;
		}
	}
	
	
	
	@Override
	public boolean onSearchRequested(){
		//打开浮动搜索框（第一个参数默认添加到搜索框的值）      
		startSearch(null, false, null, false);
		return true;
	}
	
	

	//得到搜索结果
	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		//获得搜索框里值
		query=intent.getStringExtra(SearchManager.QUERY);
		//保存搜索记录
		SearchRecentSuggestions suggestions=new SearchRecentSuggestions(this,
				SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
		suggestions.saveRecentQuery(query, null);
		CommonHelper.showProgress(this, "正在搜索: " + query);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Address address;
				int count = 0;
				while(true){
					count++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					address = searchLocationByName(query);
					Log.d(TAG, "获取经纬度");
					if(address == null && count > 5){
						Message msg1 = new Message();
						msg1.what = MSG_VIEW_LOCATIONLATLNG_FAIL;
						mHandler.sendMessage(msg1);
						break;
					}else if(address == null){
						continue;
					}else{
						break;
					}
					
				}
				
				if( address != null || count <= 5 ){
					Message msg = new Message();
					msg.what = MSG_VIEW_LOCATIONLATLNG;
					msg.obj = address;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	@Override
	public void onResume() { 
	    super.onResume(); 
	    //MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() { 
	    super.onPause(); 
	    //MobclickAgent.onPause(this); 
	}
	
	//关闭程序也关闭定位
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		fzLocation.destoryLocationManager();
	}
}

//import android.content.Context;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//import com.google.android.maps.MyLocationOverlay;
//
//public class FreeBikeActivity extends MapActivity {
//	private MapView mMapView;
//    private MyLocationOverlay mMyLocationOverlay;
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mapview);
//        
//        mMapView = (MapView)findViewById(R.id.map_view);
//        mMapView.setBuiltInZoomControls(true);
//        
//        mMyLocationOverlay = new FixedMyLocationOverlay(this, mMapView);
//        mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
//            mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
//        }});
//        mMapView.getOverlays().add(mMyLocationOverlay);
//        
//        mMapView.postInvalidate();
//		
//		// call convenience method that zooms map on our location
//		zoomToMyLocation();
//        
////        MapController mMapController = mMapView.getController();
////        //GeoPoint point = new GeoPoint((int) (120.120029 * 1e6), (int) (30.281772 * 1e6));
////        //mMapController.animateTo(point);
////        //mMapController.setCenter(point);
////        mMapController.setZoom(16);
////        GeoPoint myGeoPoint = getCurrentGeoPoint();
////        if (myGeoPoint != null) {
////        	mMapController.animateTo(myGeoPoint);
////        }
//        
//        Button btnList = (Button)findViewById(R.id.btn_list);
//        btnList.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            	 Intent intent = new Intent();
//            	 intent.setClass(FreeBikeActivity.this, StopListActivity.class);
//                 startActivity(intent);
//                 FreeBikeActivity.this.finish();
//            }
//        });
//        
//    }
//    
//    private GeoPoint getCurrentGeoPoint() { 
//    	Location location = null;
//    	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    	if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//    		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//    	}
//    	else if (locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) { 
//        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
//    	}
//    	else {
//    		return null;
//    	}
//    	
//        return new GeoPoint((int) (location.getLatitude() * 1e6), 
//                (int) (location.getLongitude() * 1e6)); 
//    }
//    
//    @Override
//	protected void onResume() {
//		super.onResume();
//		// when our activity resumes, we want to register for location updates
//		mMyLocationOverlay.enableMyLocation();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		// when our activity pauses, we want to remove listening for location updates
//		mMyLocationOverlay.disableMyLocation();
//	}
//	
//	/**
//	 * This method zooms to the user's location with a zoom level of 10.
//	 */
//	private void zoomToMyLocation() {
//		GeoPoint myLocationGeoPoint = mMyLocationOverlay.getMyLocation();
//		if (myLocationGeoPoint == null) {
//			Toast.makeText(this, "Cannot determine location", Toast.LENGTH_SHORT).show();
//			myLocationGeoPoint = new GeoPoint((int) (120.120029 * 1e6), (int) (30.281772 * 1e6));
//		}
//
//		mMapView.getController().animateTo(myLocationGeoPoint);
//		mMapView.getController().setZoom(12);
//	}
//
//	@Override
//	protected boolean isRouteDisplayed() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//}