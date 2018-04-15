package me.codetalk.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DateUtils {

	private static final long DAY_MILLIS = 24 * 60 * 60 * 1000L;
	
	public static final String FORMAT_DAY_COMPACT = "yyyyMMdd";
	public static final String FORMAT_HOUR_COMPACT = "yyyyMMddHH";
	public static final String FORMAT_MI_COMPACT = "yyyyMMddHHmm";
	public static final String FORMAT_SEC_COMPACT = "yyyyMMddHHmmss";
	public static final String FORMAT_SEC_LONG = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_MON_DAY = "MM-dd";
	
	private static final Map<String, ThreadLocal<SimpleDateFormat>> DATE_FORMATTER = new HashMap<>();
	static {
		DATE_FORMATTER.put(FORMAT_DAY_COMPACT, new ThreadLocal<SimpleDateFormat>());
		DATE_FORMATTER.put(FORMAT_HOUR_COMPACT, new ThreadLocal<SimpleDateFormat>());
		DATE_FORMATTER.put(FORMAT_MI_COMPACT, new ThreadLocal<SimpleDateFormat>());
		DATE_FORMATTER.put(FORMAT_SEC_COMPACT, new ThreadLocal<SimpleDateFormat>());
		DATE_FORMATTER.put(FORMAT_SEC_LONG, new ThreadLocal<SimpleDateFormat>());
		
		DATE_FORMATTER.put(FORMAT_MON_DAY, new ThreadLocal<SimpleDateFormat>());
	}
	
	public static Long weekStartOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    return calendar.getTimeInMillis();
	}
	
	public static Long weekEndOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);

	    return calendar.getTimeInMillis();
	}
	
	public static Long nextWeekStartOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    
	    return calendar.getTimeInMillis();
	}
	
	public static Long nextWeekEndOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    
	    return calendar.getTimeInMillis();
	}
	
	public static Long monthStartOfNow() {
		Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    
	    return calendar.getTimeInMillis();
	}

	public static Long monthEndOfNow() {
		Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    
	    return calendar.getTimeInMillis();
	}
	
	public static Long nextMonthStartOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.add(Calendar.MONTH, 1);
		
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    
	    return calendar.getTimeInMillis();
	}

	public static Long nextMonthEndOfNow() {
		Calendar calendar = getCalendarForNow();
		calendar.add(Calendar.MONTH, 1);
		
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    
	    return calendar.getTimeInMillis();
	}
	
	private static Calendar getCalendarForNow() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());
	    
	    return calendar;
	}
	
	private static Calendar getCalendarForTime(long time) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(time);
	    
	    return calendar;
	}
	
	public static Long timeMillisToMi() {
		Calendar calendar = getCalendarForNow();
	    
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    
	    return calendar.getTimeInMillis();
	}
	
	/**
	 * 
	 * @param dayOffset 示例: 1表示明天 -1表示昨天
	 * @return
	 */
	public static Long startOfDay(int dayOffset) {
		Calendar calendar = getCalendarForNow();
		
        calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    
	    return calendar.getTimeInMillis() + (dayOffset * DAY_MILLIS);
	}
	
	public static Long endOfDay(int dayOffset) {
		Calendar calendar = getCalendarForNow();
		
        calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    
	    return calendar.getTimeInMillis() + (dayOffset * DAY_MILLIS);
	}
	
	public static Long startOfDay() {
		return startOfDay(0);
	}
	
	public static Long endOfDay() {
		return endOfDay(0);
	}
	
	/********************************** Date Format **********************************/
	public static String format(long timeMillis, String format) {
		return format(new Date(timeMillis), format);
	}
	
	public static String format(Date date, String format) {
		SimpleDateFormat formatter = getDateFormatter(format);
		
		return formatter.format(date);
	}
	
	public static String formatToDay(long timeMillis) {
		return format(timeMillis, FORMAT_DAY_COMPACT);
	}
	
	public static String formatToDay(Date date) {
		return format(date, FORMAT_DAY_COMPACT);
	}
	
	public static String formatToHour(long timeMillis) {
		return format(timeMillis, FORMAT_HOUR_COMPACT);
	}
	
	public static String formatToHour(Date date) {
		return format(date, FORMAT_HOUR_COMPACT);
	}
	
	public static String formatToMi(long timeMillis) {
		return format(timeMillis, FORMAT_MI_COMPACT);
	}
	
	public static String formatToMi(Date date) {
		return format(date, FORMAT_MI_COMPACT);
	}
	
	public static String formatToSec(long timeMillis) {
		return format(timeMillis, FORMAT_SEC_COMPACT);
	}
	
	public static String formatToSec(Date date) {
		return format(date, FORMAT_SEC_COMPACT);
	}
	
	public static String readable(long timeMillis) {
		long current = System.currentTimeMillis(), secs = (current - timeMillis) / 1000;
		if(secs < 60) {
			return String.format("%ds", secs); // seconds
		} else if(secs < 60 * 60) {
			return String.format("%dm", secs / 60); // minutes 
		} else if(secs < 60 * 60 * 24) {
			return String.format("%dh", secs / 60 / 60); // hours
		} else if(secs < 60 * 60 * 24 * 8) {
			return String.format("%dd", secs / 60 / 60 / 24); // days, max: 7d
		} else if(isSameYear(current, timeMillis)) {
			return format(timeMillis, "MM-dd");
		} else {
			return format(timeMillis, "yyyy-MM-dd");
		}
	}
	
	public static boolean isSameYear(long time1, long time2) {
		Calendar c1 = getCalendarForTime(time1), c2 = getCalendarForTime(time2);
		
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}
	
	private static SimpleDateFormat getDateFormatter(String format) {
		ThreadLocal<SimpleDateFormat> formatterLocal = DATE_FORMATTER.get(format);
		
		if(formatterLocal == null) {
			return new SimpleDateFormat(format);
		} else {
			SimpleDateFormat formatter = formatterLocal.get();
			if (formatter == null) {
				formatter = new SimpleDateFormat(format);
				formatterLocal.set(formatter);
			}
			
			return formatter;
		}
	}
	
}
