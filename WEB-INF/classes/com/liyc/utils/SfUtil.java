package com.liyc.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此类用于不同数据类型间的转换及一些特殊处理
 */
public class SfUtil {
	/**
	 * 各种数据类型向字符串的转换
	 * @param or
	 * @param fType
	 * @return
	 */
	public static String format(Object or, Class<?> fType) {
		String v = "";
		if (or != null) {
			if (fType == Short.TYPE || fType == Integer.TYPE
					|| fType == Long.TYPE || fType == Byte.TYPE
					|| fType == java.lang.Long.class
					|| fType == java.lang.Short.class
					|| fType == java.lang.Byte.class
					|| fType == java.lang.Integer.class
					|| fType == int.class
					|| fType == byte.class
					|| fType == long.class
					|| fType == short.class
					) {
				v = String.format("%d", or);
			} else if (fType == String.class) {
				v = String.format("%s", or);
			} else if (fType == Boolean.class) {
				v = ((Boolean) or).booleanValue() ? "yes" : "no";
			} else if (fType == Float.class || fType == Double.TYPE) {
				v = String.format("%3.2f", or);
			} else if (fType == Date.class || fType == java.util.Date.class) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				v = sdf.format(or);
			} else if (fType == byte[].class) {
				v = "0x"+ByteUtils.asHex((byte[])or);
			}
		}
		return v;
	}

	/**
	 * 字符串转成数字
	 * 
	 * @return
	 */
	public static int parseStringToInt(String stringStr, int def) {
		int intValue = 0;
		if (isNotEmptyString(stringStr)) {
			try {
				intValue = Integer.parseInt(stringStr);
				return intValue;
			} catch (java.lang.NumberFormatException e) {
				return def;
			}
		}
		return def;
	}

	/**
	 * 字符串转成浮点型
	 * 
	 * @param str
	 * @param def
	 * @return
	 */
	public static float parseStr2Float(String str, float def) {
		if (isNotEmptyString(str)) {
			try {
				return Float.valueOf(str);
			} catch (NumberFormatException e) {
				return def;
			}
		}
		return def;
	}

	/**
	 * 浮点型转成字符串
	 * 
	 * @param f
	 * @return
	 */
	public static String parseFloat2Str(float f) {
		return String.valueOf(f);
	}
	/**
	 * 整型转换为字符串
	 * @param intValue
	 * @return
	 */
	public static String parseIntToStr(int intValue) {
		String longStr = "";
		if (isNotNullObject(intValue)) {
			try {
				longStr = String.valueOf(intValue);
			} catch (java.lang.NumberFormatException e) {
				longStr = "";
			}
		}
		return longStr;
	}
	/**
	 * 字符串转换为长整型
	 * @param str
	 * @param def
	 * @return
	 */
	public static long parseStr2Long(String str, long def) {
		if (isNotEmptyString(str)) {
			return Long.parseLong(str);
		}
		return def;
	}

	/**
	 * 验证字符串是否为空
	 */
	public static boolean isNotEmptyString(String string) {
		boolean isNotEmpty = false;
		if (isNotNullObject(string) && !"".equals(string.trim())
				&& !"null".equals(string.trim())) {
			isNotEmpty = true;
		}
		return isNotEmpty;
	}
	/**
	 * 验证对象数组是否为空
	 * @param strings
	 * @return
	 */
	public static boolean isNotEmptyString(Object[] strings) {
		boolean isNotEmpty = true;
		if (isNotNullObject(strings)) {
			for (int i = 0; i < strings.length; i++) {
				if (!SfUtil.isNotNullObject(strings[i])) {
					isNotEmpty = false;
				}
			}
		}
		return isNotEmpty;
	}
	/**
	 * 验证字符串数组是否为空
	 * @param strings
	 * @return
	 */
	public static boolean isNotEmptyStringArray(String[] strings) {
		boolean isNotEmpty = true;
		if (isNotNullObject(strings)) {
			for (int i = 0; i < strings.length; i++) {
				if (!SfUtil.isNotEmptyString(strings[i])) {
					isNotEmpty = false;
				}
			}
		}
		return isNotEmpty;
	}
	/**
	 * 判断对象是否为空
	 * @param object
	 * @return
	 */
	public static boolean isNotNullObject(Object object) {
		boolean isNotNull = false;
		if (object != null) {
			isNotNull = true;
		}
		return isNotNull;
	}
	/**
	 * 长整型转化为字符串
	 * @param longValue
	 * @return
	 */
	public static String parseLongToString(Long longValue) {
		String longStr = "";
		if (isNotNullObject(longValue)) {
			try {
				longStr = String.valueOf(longValue);
			} catch (java.lang.NumberFormatException e) {
				longStr = "";
			}
		}
		return longStr;
	}
	/**
	 * 字符串分割成字符串数组
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String[] parseStringToStringArray(String str, String regex) {
		String[] strs = new String[] {};
		if (isNotEmptyString(str)) {
			str = str.trim();
			strs = str.split(regex);
		}
		return strs;
	}
	/**
	 * 字符串分割为长整型list
	 * @param str
	 * @param regex
	 * @return
	 */
	public static List<Long> parse2LongList(String str, String regex) {
		String[] strs = new String[] {};
		List<Long> longs = new ArrayList<Long>();
		if (isNotEmptyString(str)) {
			str = str.trim();
			strs = str.split(regex);
			for (int i = 0; i < strs.length; i++) {
				String s = strs[i];
				longs.add(parseStr2Long(s, 0));
			}
		}
		return longs;
	}

	/**
	 * 把字符串数组转成一个map，每个字符串元素如：{"key:value"}
	 * 
	 * @param strs
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static Map parseStrs2Map(String[] strs) {
		Map map = null;
		if (strs != null) {
			map = new HashMap();
			for (int i = 0; i < strs.length; i++) {
				int index = strs[i].indexOf(":");
				String a = strs[i].substring(0, strs[i].indexOf(":"));
				String b = strs[i].substring(strs[i].indexOf(":") + 1,
						strs[i].length());
				map.put(a, b);
			}
		}
		return map;
	}
	/**
	 * 判断list是否为空
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmptyArray(List list) {
		boolean isNotEmpty = false;
		if (isNotNullObject(list) && list.size() > 0) {
			isNotEmpty = true;
		}
		return isNotEmpty;

	}

	/**
	 * 获取一个新的map对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getNewMap(String key, Object value) {
		Map map = new HashMap();
		map.put(key, value);
		return map;
	}

	/**
	 * 获取一个新的map对象
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getNewMap(String[][] colMap) {
		Map map = new HashMap();
		for (int i = 0; i < colMap.length; i++) {
			if (isNotEmptyString(colMap[i][0])
					&& isNotEmptyString(colMap[i][1])) {
				map.put(colMap[i][0], colMap[i][1]);
			}
		}
		return map;

	}

	/**
	 * 字符串强转成短整型
	 * 
	 * @return
	 */
	public static short parseStr2Short(String str, int def) {
		if (isNotEmptyString(str)) {
			return Short.parseShort(str);
		}
		return (short) def;
	}
	/**
	 * 字符串转换为byte
	 * @param str
	 * @param def
	 * @return
	 */
	public static Byte parseStr2Byte(String str, byte def) {
		if (isNotEmptyString(str)) {
			return Byte.parseByte(str);
		}
		return def;
	}

	/**
	 * 判断一个数字是否在一个范围内
	 * 
	 * @param current
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean rangeInDefined(int current, int min, int max) {
		return Math.max(min, current) == Math.min(current, max);
	}

	/**
	 * short转成string
	 * 
	 * @param checkIn
	 * @return
	 */
	public static String parseShort2Str(short shortValue) {
		String shortStr = "";
		if (isNotNullObject(shortValue)) {
			try {
				shortStr = String.valueOf(shortValue);
			} catch (java.lang.NumberFormatException e) {
				shortStr = "";
			}
		}
		return shortStr;
	}

	/**
	 * 根据size，自动在id前面补上0，并重新生成一个新的字符串
	 * 
	 * @param idSize
	 * @return
	 */
	public static String fillZero(String id, int size) {
		int fillNum = size - id.length();
		for (int i = 0; i < fillNum; i++) {
			id = "0" + id;
		}
		return id;
	}

	

	/**
	 * 获取字符串编码类型
	 */
	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}
	/**
	 * 格式化字符串地址（010016008254  》 10.16.8.254）
	 * @param address
	 * @return
	 */
	public static String formatAddress(String address){
		int one = Integer.parseInt(address.substring(0, 3));
		int two = Integer.parseInt(address.substring(3, 6));
		int three = Integer.parseInt(address.substring(6, 9));
		int four = Integer.parseInt(address.substring(9, 12));
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(one);
		stringBuffer.append(".");
		stringBuffer.append(two);
		stringBuffer.append(".");
		stringBuffer.append(three);
		stringBuffer.append(".");
		stringBuffer.append(four);
		return stringBuffer.toString();
	}
}
