package com.liyc.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 日期格式转换、处理工具类
 */
@SuppressWarnings("static-access")
public class DateUtils {
	private static Logger logger = Logger.getLogger(DateUtils.class);

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int getDaysDiff(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 对比两个时间相差多少毫秒
	 */
	public static long getTimeDiff(Date from, Date to) {
		long f = from.getTime();
		long t = to.getTime();
		return Math.abs(f - t);
	}

	/**
	 * 计算与当前的时间差
	 */
	public static long getCurrTimeDiff(Date target) {
		return Math.abs(System.currentTimeMillis() - target.getTime());
	}

	/**
	 * 获取当前时间（年-月-日 时：分：秒）
	 * 
	 * @return 返回日期格式
	 */
	public static Date getCurrtDoneTime() {
		Date resultDate = new Date();
		try {
			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = dateFm.format(date);
			resultDate = dateFm.parse(dateStr);
		} catch (Exception e) {
			throw new RuntimeException("时间转换错误!", e);
		}
		return resultDate;
	}

	/**
	 * 获取当前时间按自定义格式
	 * 
	 * @return 返回日期格式
	 */
	public static Date getCurrtDoneTime(String parrent) {
		Date resultDate = new Date();
		try {
			Date date = new Date();
			if (SfUtil.isNotEmptyString(parrent)) {
				parrent = "yyyy-MM-dd HH:mm:ss";
			}
			SimpleDateFormat dateFm = new SimpleDateFormat(parrent);
			String dateStr = dateFm.format(date);
			resultDate = dateFm.parse(dateStr);
		} catch (Exception e) {
			throw new RuntimeException("时间转换错误!", e);
		}
		return resultDate;
	}

	/**
	 * 格式化时间
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String parseDate2Str(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = null;
		if (SfUtil.isNotEmptyString(pattern)) {
			format = new SimpleDateFormat(pattern);
		} else {
			format = new SimpleDateFormat("yyyy-MM-dd");
		}
		String str = format.format(date);
		return str;
	}

	/**
	 * 获取当前日期，返回字符串格式
	 * 
	 * @return
	 */
	public static String getCurrtDoneTimeString() {

		try {
			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return dateFm.format(date);

		} catch (Exception e) {
			throw new RuntimeException("时间转换错误!", e);
		}

	}

	/**
	 * 获取中文完整日期
	 * 
	 * @return
	 */
	public static String getChineseDate(Date date) {
		String d = parseDate2Str(date, "");
		return d.substring(0, 4) + "年" + d.substring(5, 7) + "月" + d.substring(8, 10) + "日" + d.substring(10, 13) + "时"
				+ d.substring(14, 16) + "分" + d.substring(17, 19) + "秒";
	}

	/**
	 * 获取当前日期，返回yyyyMMddHHmmss格式
	 * 
	 * @return
	 */
	public static String getCurrtDoneTimeStringAll() {

		try {
			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss");
			return dateFm.format(date);

		} catch (Exception e) {
			throw new RuntimeException("时间转换错误!", e);
		}

	}

	/**
	 * 字符串转成日期格式
	 * 
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static Date formatStringToDate(String string, String pattern) {
		Date date = new Date();
		if (SfUtil.isNotEmptyString(string)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			if (SfUtil.isNotEmptyString(pattern)) {
				dateFormat.applyPattern(pattern);
			} else {
				dateFormat.applyPattern("yyyy-MM-dd");
			}
			try {
				date = dateFormat.parse(string);
				return date;
			} catch (ParseException e) {
				logger.error(e);
				logger.error("时间转换错误");
				return null;
			}
		}
		return date;
	}

	/**
	 * 字符串（yyyyMMddHHmmss）转成日期格式
	 * 
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static Date formatStringAllToDate(String string, String pattern) {
		Date date = null;
		if (SfUtil.isNotEmptyString(string)) {
			String dateTime = "";
			dateTime = dateTime + string.substring(0, 4) + "-";
			dateTime = dateTime + string.substring(4, 6) + "-";
			dateTime = dateTime + string.substring(6, 8) + " ";
			dateTime = dateTime + string.substring(8, 10) + ":";
			dateTime = dateTime + string.substring(10, 12) + ":";
			dateTime = dateTime + string.substring(12, 14);
			date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat();
			if (SfUtil.isNotEmptyString(pattern)) {
				dateFormat.applyPattern(pattern);
			} else {
				dateFormat.applyPattern("yyyy-MM-dd");
			}
			try {
				date = dateFormat.parse(dateTime);
				return date;
			} catch (ParseException e) {
				logger.error(e);
				logger.error("时间转换错误");
				return null;
			}
		}
		return date;
	}

	/**
	 * 获取当前系统时间梭
	 * 
	 * @return
	 */
	public static String getCurrentTimeMillis() {
		Long currentTimeMillis = System.currentTimeMillis();
		return SfUtil.parseLongToString(currentTimeMillis);
	}

	/**
	 * 获取当前月份
	 */
	public static int getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		int month = c.get(c.MONTH) + 1;
		return month;
	}

	/**
	 * 获取当前季节
	 */
	public static String getCurrentSeason() {
		int month = getCurrentMonth();
		String season = "4";
		if (month > 2 && month < 6) {
			season = "1";
		} else if (month > 5 && month < 9) {
			season = "2";
		} else if (month > 8 && month < 12) {
			season = "3";
		}
		return season;
	}

	/**
	 * 获取某一天yyyy-MM-dd
	 */
	public static String getSomeDay(Date date, int days) {
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		calerdar.add(calerdar.DATE, days);
		return parseDate2Str(calerdar.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取当天是本周的第几天
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		return calerdar.get(calerdar.DAY_OF_WEEK);
	}

	/**
	 * 获得上周最后一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getLastWeekEndDay(Date date) {
		// 获取当天是一周的第几天
		int dayOfWeek = getDayOfWeek(date);
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		// 获取上星期的最后一天
		calerdar.add(calerdar.DATE, -dayOfWeek);
		Date monday = calerdar.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday;
	}

	/**
	 * 获得上周第一天的日期的日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getLastWeekFirstDay(Date date) {
		// 获取当天是一周的第几天
		int dayOfWeek = getDayOfWeek(date);
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		// 获取上星期的最后一天
		calerdar.add(calerdar.DATE, -dayOfWeek - 6);
		Date firstDay = calerdar.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preFirst = df.format(firstDay);
		return preFirst;
	}

	/**
	 * 获取一个月的第几周
	 */
	public static int getWeekOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(cal.WEEK_OF_MONTH);
	}

	/**
	 * 获取一年中的第几周
	 */
	public static int getWeekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(cal.WEEK_OF_YEAR);
	}

	/**
	 * 获取当天是本月的第几天
	 */
	public static int getDayOfMonth(Date date) {
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		return calerdar.get(calerdar.DAY_OF_MONTH);
	}

	/**
	 * 获取上一个月
	 */
	public static String getLastMonth(Date date) {
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		calerdar.add(calerdar.MONTH, -1);
		String lastMonth = parseDate2Str(calerdar.getTime(), "yyyy-MM");
		return lastMonth;
	}

	/**
	 * 获取当前月
	 */
	public static String getThisMonth(Date date) {
		String thisMonth = parseDate2Str(date, "yyyy-MM");
		return thisMonth;
	}

	/**
	 * 获取每个月的第一天
	 */
	public static String getFirstOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.DAY_OF_MONTH, 1);
		return DateUtils.parseDate2Str(calendar.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取每个月的最后一天
	 */
	public static String getEndOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateUtils.parseDate2Str(calendar.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取本月推前或者往后的几个月
	 */
	public static Date getSomeMonth(Date date, int months) {
		Calendar calerdar = Calendar.getInstance();
		calerdar.setTime(date);
		calerdar.add(calerdar.MONTH, months);
		return calerdar.getTime();
	}

	/**
	 * 获取一个季度最开始月份
	 */
	public static String getFirstMonthOfSeason(int season) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (season == 1) {
			calendar.set(calendar.MONTH, 1);
		} else if (season == 2) {
			calendar.set(calendar.MONTH, 4);
		} else if (season == 3) {
			calendar.set(calendar.MONTH, 7);
		} else {
			calendar.set(calendar.MONTH, 10);
		}
		return getFirstOfMonth(calendar.getTime());
	}

	/**
	 * 获取一个季度的最末月份
	 */
	public static String getEndMonthOfSeason(int season) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (season == 1) {
			calendar.set(calendar.MONTH, 3);
		} else if (season == 2) {
			calendar.set(calendar.MONTH, 6);
		} else if (season == 3) {
			calendar.set(calendar.MONTH, 9);
		} else {
			calendar.set(calendar.MONTH, 12);
		}
		return getEndOfMonth(calendar.getTime());
	}

	/**
	 * 获取一天中四个区间内的的开始时间 一天分成四个区间(0-6,7-12,13-18,19-24)
	 */
	public static String getFirstHourOfDay(int interval) {
		int hours = 1;
		if (interval == 1) {
			hours = 0;
		} else if (interval == 2) {
			hours = 7;
		} else if (interval == 3) {
			hours = 13;
		} else {
			hours = 19;
		}
		return getSomeHour(hours);
	}

	/**
	 * 获取当前时间在一天中四个区间内是第几个区间
	 */
	public static int getIntervalOfDay(int hour) {
		int interval = 1;
		if (hour >= 0 && hour < 6) {
			interval = 1;
		} else if (hour >= 6 && hour < 12) {
			interval = 2;
		} else if (hour >= 12 && hour < 18) {
			interval = 3;
		} else {
			interval = 4;
		}
		return interval;
	}

	/**
	 * 获取一天中四个区间内的的结束时间。 一天分成四个区间(0-6,7-12,13-18,19-24)
	 */
	public static String getEndHourOfDay(int interval) {
		int hours = 1;
		if (interval == 1) {
			hours = 6;
		} else if (interval == 2) {
			hours = 12;
		} else if (interval == 3) {
			hours = 18;
		} else {
			hours = 24;
		}
		return getSomeHour(hours);
	}

	@SuppressWarnings("deprecation")
	public static String getSomeHour(int hours) {
		Date date = new Date();
		date.setHours(hours);
		date.setMinutes(0);
		date.setSeconds(0);
		return parseDate2Str(date, "yyyy-MM-dd HH:mm:ss");
	}

	@SuppressWarnings("deprecation")
	public static int getCurHour(Date date) {
		return date.getHours();
	}

	/**
	 * 获取上、中、下旬
	 */
	public static int getPeriodOfMonth(Date date) {
		int days = getDayOfMonth(date);
		if (days < 11) {
			return 1;
		} else if (days > 10 && days < 21) {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * 获取上、中、下旬对应的区间(开始日期)
	 */
	public static String getStartPeriodOfMonth(Date date, int period) {
		int days = getDayOfMonth(date);
		if (period == 1) {
			return getSomeDay(date, -days + 1);
		} else if (period == 2) {
			return getSomeDay(date, 11 - days);
		} else {
			return getSomeDay(date, 21 - days);
		}
	}

	/**
	 * 获取上、中、下旬对应的区间(结束日期)
	 */
	public static String getEndPeriodOfMonth(Date date, int period) {
		int days = getDayOfMonth(date);
		if (period == 1) {
			return getSomeDay(date, 10 - days);
		} else if (period == 2) {
			return getSomeDay(date, 20 - days);
		} else {
			return getEndOfMonth(date);
		}
	}

	/**
	 * 获取年、月、日
	 */
	public static String getSubDate(Date date, int sub) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (sub == 1) {
			return cal.get(cal.YEAR) + "";
		} else if (sub == 2) {
			return (cal.get(cal.MONTH) + 1) + "";
		} else if (sub == 3) {
			return cal.get(cal.DATE) + "";
		} else if (sub == 4) {
			String dateStr = parseDate2Str(date, "yyyy-MM-dd HH:mm:ss");
			if (SfUtil.isNotEmptyString(dateStr)) {
				return dateStr.substring(11, 13);
			}
			return "";
		} else {
			return parseDate2Str(cal.getTime(), "yyyy-MM-dd");
		}
	}
	/**
	 * 获取上、中、下旬对应的区间
	 * @param period
	 * @return
	 */
	public static String getPeriodName(int period) {
		if (period == 1) {
			return "上旬";
		} else if (period == 2) {
			return "中旬";
		} else {
			return "下旬";
		}
	}
	/**
	 * 在指定时间上加上一定时间
	 * @param inputDate
	 * @param len
	 * @param type
	 * @return
	 */
	public static Date addDate(Date inputDate, int len, char type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(inputDate);
		if (type == 'Y') {
			calendar.add(Calendar.YEAR, len);

		} else if (type == 'M') {
			calendar.add(Calendar.MONTH, len);
		} else if (type == 'D') {
			calendar.add(Calendar.DATE, len);
		} else if (type == 'H') {
			calendar.add(Calendar.HOUR, len);
		} else if (type == 'm') {
			calendar.add(Calendar.MINUTE, len);
		} else if (type == 's') {
			calendar.add(Calendar.SECOND, len);
		}
		return calendar.getTime();
	}

	/**
	 * 获取时间的小时数
	 */
	public static int getHouse(Date day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(day);
		return calendar.get(calendar.HOUR_OF_DAY);
	}

}
