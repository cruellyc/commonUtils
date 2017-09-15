package com.liyc.utils;

import java.io.ByteArrayOutputStream;

/**
 * 进制之间的转换
 */
public class BinHexOctUtil {
	// 二进制
	public static final int SCALE_BINARY = 2;
	// 八进制
	public static final int SCALE_OCTAL = 8;
	// 十六进制
	public static final int SCALE_HEX = 16;

	/**
	 * 字符串转换为16进制字符串 1.遍历字符串 2.字符串转成整型 3.整型转16进制字符 4.16进制字符串拼接
	 */
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	/**
	 * 1.两个16进制字符等于一个字节 2.将byte数组转成字符串 16进制编码转成字符串
	 */
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/**
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * 十进制转成16进制
	 */
	public static String toHexString(int i) {
		return Integer.toHexString(i);
	}

	/**
	 * 十进制转成8进制
	 */
	public static String toOctalString(int i) {
		return Integer.toOctalString(i);
	}

	/**
	 * 十进制转成2进制
	 */
	public static String toBinaryString(int i) {
		return Integer.toBinaryString(i);
	}

	/**
	 * 由2/8/16进制转成十进制
	 */
	public static int toDecimalString(String str, int binHexOct) {
		return Integer.valueOf(str, binHexOct);
	}

}
