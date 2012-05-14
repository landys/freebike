package com.hippocrene.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class CommonUtil {
	public static Boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}
	
	public static SimpleDateFormat getSimpleDataFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public static long stringDataToLong(SimpleDateFormat sdf, String strDate) {
		long time = 0;
		
		try {
			Date dt = sdf.parse(strDate);
			time = dt.getTime();
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		
		return time;
	}
	
	public static String idsToString(ArrayList<String> ids) {
		if (ids == null || ids.size() == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<ids.size(); ++i) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(ids.get(i));
		}
		
		return sb.toString();
	}
}
