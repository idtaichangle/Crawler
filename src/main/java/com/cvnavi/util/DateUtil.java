package com.cvnavi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static final String[] formats19 = { "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss" };
	private static final String[] formats16 = { "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm" };
	private static final String[] formats14 = { "yy-MM-dd HH:mm", "yy/MM/dd HH:mm" };
	private static final String[] formats11 = { "MM-dd HH:mm", "MM/dd HH:mm" };
	private static final String[] formats10 = { "MM-dd H:mm", "MM/dd H:mm" };

	static Calendar calendar=Calendar.getInstance();
	
	public static Date parse(String d) {
		if (d != null) {
			String[] formats = formats19;
			if (d.length() == 19) {
				formats = formats19;
			} else if (d.length() == 16) {
				formats = formats16;
			} else if (d.length() == 14) {
				formats = formats14;
			} else if (d.length() == 11) {
				formats = formats11;
			}else if (d.length() == 10) {
				formats = formats10;
			}
			for (String parse : formats) {
				SimpleDateFormat sdf = new SimpleDateFormat(parse);
				try {
					Date date=sdf.parse(d);
					if(formats==formats11){
						calendar.setTime(date);
						calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
						date=calendar.getTime();
					}
					return date;
				} catch (ParseException e) {
				}
			}
		}
		return null;
	}
}
