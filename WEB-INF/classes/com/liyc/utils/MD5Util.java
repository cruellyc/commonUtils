package com.liyc.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
md5加密算法，有16位、32位加密，分别生成32位、64位密文
*/
public class MD5Util {
	public static String Md5(String plainText) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
				i += 256;
				if (i < 16)
				buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			 result = buf.toString();  //md5 32bit
			// result = buf.toString().substring(8, 24))); //md5 16bit
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
}
