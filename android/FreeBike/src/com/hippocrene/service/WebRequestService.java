package com.hippocrene.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hippocrene.model.StationBikeInfo;
import com.hippocrene.model.StationInfo;
import com.hippocrene.util.CommonUtil;
import com.hippocrene.util.Constants;

public class WebRequestService {
	
	/**
	 * @param cityId
	 * @return
	 */
	public long getStationDataVersion(int cityId) {
		long version = 0;
		
		String stationUrl = String.format(Constants.GetVersionUrl, cityId);
		String retSrc = sendhttpRequest(stationUrl);
		
		if (!CommonUtil.isEmpty(retSrc)) {
			try {
				JSONArray result = new JSONArray(retSrc);
				if (result.length() > 0) {
					JSONObject jobj = (JSONObject) result.opt(0);
					version = jobj.getLong("verID");
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		return version;
	}
	
	/**
	 * @param cityId
	 * @return
	 */
	public ArrayList<StationInfo> getStationInfos(int cityId) {
		ArrayList<StationInfo> stationInfos = new ArrayList<StationInfo>();
		
		String stationUrl = String.format(Constants.GetStationsUrl, cityId);
		String retSrc = sendhttpRequest(stationUrl);
		
		if (!CommonUtil.isEmpty(retSrc)) {
			try {
				JSONArray result = new JSONArray(retSrc);
				SimpleDateFormat sdf = CommonUtil.getSimpleDataFormat();
				for (int i=0; i<result.length(); ++i) {
					JSONObject jobj = (JSONObject) result.opt(i);
					StationInfo stInfo = new StationInfo();
					stInfo.setStationId(jobj.getString("stID"));
					stInfo.setName(jobj.getString("nm"));
					stInfo.setAddress(jobj.getString("addr"));
					stInfo.setTelephone(jobj.getString("tel"));
					stInfo.setServiceTime(jobj.getString("svTm"));
					stInfo.setBikeSum(jobj.getInt("sum"));
					stInfo.setLatitude(jobj.getDouble("lat"));
					stInfo.setLongtitude(jobj.getDouble("lng"));
					stInfo.setUpdateTime(CommonUtil.stringDataToLong(sdf, jobj.getString("updtTm")));
					stInfo.setTip(jobj.getString("tp"));
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		return stationInfos;
	}
	
	/**
	 * If stationIds is null or empty, return all the station bile infos.
	 * @param cityId
	 * @param stationIds
	 * @return
	 */
	public ArrayList<StationBikeInfo> getStationInfos(int cityId, ArrayList<String> stationIds) {
		ArrayList<StationBikeInfo> bikeInfos = new ArrayList<StationBikeInfo>();
		
		String bikesUrl = null;
		if (stationIds == null || stationIds.size() == 0) {
			bikesUrl = String.format(Constants.GetAllBikesUrl, cityId);
		}
		else {
			bikesUrl = String.format(Constants.GetBikesUrl, cityId, CommonUtil.idsToString(stationIds));
		}
		
		String retSrc = sendhttpRequest(bikesUrl);
		
		if (!CommonUtil.isEmpty(retSrc)) {
			try {
				JSONArray result = new JSONArray(retSrc);
				SimpleDateFormat sdf = CommonUtil.getSimpleDataFormat();
				for (int i=0; i<result.length(); ++i) {
					JSONObject jobj = (JSONObject) result.opt(i);
					StationBikeInfo sbInfo = new StationBikeInfo();
					sbInfo.setStationId(jobj.getString("stID"));
					sbInfo.setAvCount(jobj.getInt("avCt"));
					sbInfo.setVctCount(jobj.getInt("vctCt"));
					sbInfo.setUpdateTime(CommonUtil.stringDataToLong(sdf, jobj.getString("updtTm")));
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		return bikeInfos;
	}
	
	private String sendhttpRequest(String url) {
		String retSrc = null;
	
		HttpPost request = new HttpPost(url); 

		HttpResponse httpResponse;
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				retSrc = EntityUtils.toString(httpResponse.getEntity());  
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retSrc;
	}
}
