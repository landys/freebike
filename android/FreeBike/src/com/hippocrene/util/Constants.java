package com.hippocrene.util;

public final class Constants {
	private static final String WebServer = "http://hippocrene.sinaapp.com/";
	public static final String GetVersionUrl = WebServer + "getVersion.php?city=%d";
	public static final String GetStationsUrl = WebServer + "getStations.php?city=%d";
	public static final String GetAllBikesUrl = WebServer + "getBikes.php?city=%d";
	public static final String GetBikesUrl = WebServer + "getBikes.php?city=%d&stations=%s";
}
