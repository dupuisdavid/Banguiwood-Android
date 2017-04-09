package com.afrikawood.banguiwood.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtilities {
	
	public static Date convertStringToDate(String dateString, String dateFormat) {

		Date date = null;
		
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
        return date;
	}
	
	public static String convertDateToString(Date date, String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
		String dateString = simpleDateFormat.format(date);
        return dateString;
	    
	}

}
