package com.hippocrene.model;

public class StationBikeInfo {
	private String stationId;
	
	private int avCount;
	
	private int vctCount;
	
	private long updateTime;

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public int getAvCount() {
		return avCount;
	}

	public void setAvCount(int avCount) {
		this.avCount = avCount;
	}

	public int getVctCount() {
		return vctCount;
	}

	public void setVctCount(int vctCount) {
		this.vctCount = vctCount;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	
	
}
