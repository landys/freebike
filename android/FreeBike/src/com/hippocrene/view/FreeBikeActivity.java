package com.hippocrene.view;

import java.io.IOException;
import java.util.ArrayList;
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
import com.hippocrene.R;
import com.hippocrene.model.StationInfo;
import com.hippocrene.service.WebRequestService;
import com.hippocrene.util.CommonHelper;
import com.hippocrene.view.MyLocationManager.LocationCallBack;

public class FreeBikeActivity extends MapActivity implements LocationCallBack,
		OnClickListener {
	private final String TAG = "FreeBikeActivity";
	private MapView mMapView;
	private MapController mMapCtrl;
	private View mPopView;
	private MyLocationManager mMyLocationManager;
	private PinItemizedOverlay mMyLocationOverlay;
	private PinItemizedOverlay mLongPressOverlay;
	private PinItemizedOverlay mStationOverlay;
	private PinItemizedOverlay mAddressOverlay;

	private List<Overlay> mapOverlays;
	private String mQuery;
	private GeoPoint mLocPoint;
	
	private WebRequestService mWebService;

	public final int MSG_VIEW_LONGPRESS = 10001;
	public final int MSG_VIEW_ADDRESSNAME = 10002;
	public final int MSG_VIEW_ADDRESSNAME_FAIL = 10004;
	public final int MSG_VIEW_LOCATIONLATLNG = 10003;
	public final int MSG_VIEW_LOCATIONLATLNG_FAIL = 10005;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mapview);

		ImageButton btnMyLocation = (ImageButton) findViewById(R.id.my_location);
		ImageButton btnAddrLocation = (ImageButton) findViewById(R.id.addr_location);
		Button btnSearch = (Button) findViewById(R.id.search);

		btnMyLocation.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnAddrLocation.setOnClickListener(this);

		Drawable myLocationDrawable = getResources().getDrawable(R.drawable.my_location);
		Drawable pinGreenDrawable = getResources().getDrawable(R.drawable.pin_green);
		Drawable pinOrangeDrawable = getResources().getDrawable(R.drawable.pin_orange);
		Drawable markerRedDrawable = getResources().getDrawable(R.drawable.marker_red);
		
		mMapView = (MapView) findViewById(R.id.map_view);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);
		initPopView();
		mMapCtrl = mMapView.getController();
		mMyLocationOverlay = new PinItemizedOverlay(myLocationDrawable, this, mMapView, mPopView, mMapCtrl);
		mLongPressOverlay = new PinItemizedOverlay(pinGreenDrawable, this, mMapView, mPopView, mMapCtrl);
		mStationOverlay = new PinItemizedOverlay(markerRedDrawable, this, mMapView, mPopView, mMapCtrl);
		mAddressOverlay = new PinItemizedOverlay(pinOrangeDrawable, this, mMapView, mPopView, mMapCtrl);
		
		mapOverlays = mMapView.getOverlays();
		mapOverlays.add(new LongPressOverlay(this, mMapView, mHandler, mMapCtrl));

		// Hangzhou.
		GeoPoint cityLocPoint = new GeoPoint((int) (30.281772 * 1e6), (int) (120.120029 * 1e6));
		mMapCtrl.animateTo(cityLocPoint);
		mMapCtrl.setZoom(16);
		MyLocationManager.init(FreeBikeActivity.this.getApplicationContext(),
				FreeBikeActivity.this);
		mMyLocationManager = MyLocationManager.getInstance();

		// button switch to list
		Button btnList = (Button) findViewById(R.id.btn_list);
		btnList.setOnClickListener(this);
		
		mWebService = new WebRequestService(this);
		
		initBikeStations();
	}
	
	private void initBikeStations() {
		ArrayList<StationInfo> stationInfos = mWebService.getStationInfosForLocal(0);
		if (stationInfos != null && stationInfos.size() > 0) {
			for (int i=0; i<stationInfos.size() && i<100; ++i) {
				StationInfo stationInfo = stationInfos.get(i);
				OverlayItem overlayItem = new OverlayItem(new GeoPoint((int)(stationInfo.getLatitude() * 1e6), (int)(stationInfo.getLongtitude() * 1e6)), 
						stationInfo.getName(), stationInfo.getAddress());
				mStationOverlay.addOverlay(overlayItem);
			}
			mapOverlays.add(mStationOverlay);
			mMapView.invalidate();
		}
	}

	private void initPopView() {
		if (null == mPopView) {
			mPopView = getLayoutInflater().inflate(R.layout.overlay_popup, null);
			mMapView.addView(mPopView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			mPopView.setVisibility(View.GONE);
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCurrentLocation(Location location) {
		Log.d(TAG, "onCurrentLocationy");

		GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		OverlayItem overlayitem = new OverlayItem(point, "我的位置", "");
		mMapCtrl.setZoom(16);
		if (mMyLocationOverlay.size() > 0) {
			mMyLocationOverlay.removeOverlay(0);
		}
		mMyLocationOverlay.addOverlay(overlayitem);
		mapOverlays.add(mMyLocationOverlay);
		mMapCtrl.animateTo(point);
	}
	

	/**
	 * Get address by latitude and longtitude.
	 * 
	 * @param point
	 * @return
	 */
	private String getLocationAddress(GeoPoint point) {
		String add = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6,
					1);
			Address address = addresses.get(0);
			int maxLine = address.getMaxAddressLineIndex();
			if (maxLine >= 2) {
				add = address.getAddressLine(1) + address.getAddressLine(2);
			} else {
				add = address.getAddressLine(1);
			}
		} catch (IOException e) {
			add = "";
			e.printStackTrace();
		}
		return add;
	}

	private Address searchLocationByName(String addressName) {
		if (addressName == null || addressName.length() == 0) {
			return null;
		}
		
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.CHINA);
		try {
			List<Address> addresses = geoCoder.getFromLocationName(addressName,
					1);
			Address address_send = null;
			for (Address address : addresses) {
				mLocPoint = new GeoPoint((int) (address.getLatitude() * 1E6),
						(int) (address.getLongitude() * 1E6));
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
			case MSG_VIEW_LONGPRESS:
			{
				if (null == mLocPoint)
					return;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String addressName = "";

						int count = 0;
						while (true) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							count++;
							addressName = getLocationAddress(mLocPoint);
							Log.d(TAG, "获取地址名称");
							// request at most 5 times
							if ("".equals(addressName) && count > 5) {
								Message msg1 = new Message();
								msg1.what = MSG_VIEW_ADDRESSNAME_FAIL;
								mHandler.sendMessage(msg1);
								break;
							} else if ("".equals(addressName)) {
								continue;
							} else {
								break;
							}

						}
						if (!"".equals(addressName) || count < 5) {
							Message msg = new Message();
							msg.what = MSG_VIEW_ADDRESSNAME;
							msg.obj = addressName;
							mHandler.sendMessage(msg);
						}
					}
				}).start();
				OverlayItem overlayitem = new OverlayItem(mLocPoint, "地址名称", "正在地址加载...");
				if (mLongPressOverlay.size() > 0) {
					mLongPressOverlay.removeOverlay(0);
				}
				mPopView.setVisibility(View.GONE);
				mLongPressOverlay.addOverlay(overlayitem);
				mLongPressOverlay.setFocus(overlayitem);
				mapOverlays.add(mLongPressOverlay);
				mMapCtrl.animateTo(mLocPoint);
				mMapView.invalidate();
			}
				break;
			case MSG_VIEW_ADDRESSNAME: {
				// fetch the address
				TextView desc = (TextView) mPopView
						.findViewById(R.id.map_bubbleText);
				desc.setText((String) msg.obj);
				mPopView.setVisibility(View.VISIBLE);
			}
				break;
			case MSG_VIEW_ADDRESSNAME_FAIL: {
				TextView desc = (TextView) mPopView
						.findViewById(R.id.map_bubbleText);
				desc.setText("获取地址失败");
				mPopView.setVisibility(View.VISIBLE);
			}
				break;
			case MSG_VIEW_LOCATIONLATLNG: {
				CommonHelper.closeProgress();
				Address address = (Address) msg.obj;
				mLocPoint = new GeoPoint((int) (address.getLatitude() * 1E6),
						(int) (address.getLongitude() * 1E6));
				OverlayItem overlayitem = new OverlayItem(mLocPoint, "地址名称",
						address.getAddressLine(1));
				if (mLongPressOverlay.size() > 0) {
					mLongPressOverlay.removeOverlay(0);
				}
				mPopView.setVisibility(View.GONE);
				mLongPressOverlay.addOverlay(overlayitem);
				mLongPressOverlay.setFocus(overlayitem);
				mapOverlays.add(mLongPressOverlay);
				mMapCtrl.animateTo(mLocPoint);
				mMapView.invalidate();
			}
				break;
			case MSG_VIEW_LOCATIONLATLNG_FAIL: {
				CommonHelper.closeProgress();
				Toast.makeText(FreeBikeActivity.this, "搜索地址失败",
						Toast.LENGTH_SHORT).show();
			}
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_location: {
			if (mMyLocationOverlay != null) {
				GeoPoint point = mMyLocationOverlay.getCenter();
			
				mMapCtrl.animateTo(point);
			}
		}
			break;
		case R.id.search: {
			onSearchRequested();
		}
			break;
		case R.id.btn_list: {
			Intent intent = new Intent();
			intent.setClass(FreeBikeActivity.this, StationListActivity.class);
			startActivity(intent);
			//FreeBikeActivity.this.finish();
		}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onSearchRequested() {
		// open the float search bar, the first parameter is the value of the search input box.
		startSearch(null, false, null, false);
		return true;
	}

	// get the search value.
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// get the value in search input box.
		mQuery = intent.getStringExtra(SearchManager.QUERY);
		// save the search history.
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionProvider.AUTHORITY,
				SearchSuggestionProvider.MODE);
		suggestions.saveRecentQuery(mQuery, null);
		CommonHelper.showProgress(this, "正在搜索: " + mQuery);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Address address;
				int count = 0;
				while (true) {
					count++;
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					address = searchLocationByName(mQuery);
					Log.d(TAG, "获取经纬度");
					if (address == null && count > 5) {
						Message msg1 = new Message();
						msg1.what = MSG_VIEW_LOCATIONLATLNG_FAIL;
						mHandler.sendMessage(msg1);
						break;
					} else if (address == null) {
						continue;
					} else {
						break;
					}

				}

				if (address != null || count <= 5) {
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
		// MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	// 关闭程序也关闭定位
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMyLocationManager.destoryLocationManager();
	}

	public GeoPoint getLocPoint() {
		return mLocPoint;
	}

	public void setLocPoint(GeoPoint locPoint) {
		this.mLocPoint = locPoint;
	}
}
