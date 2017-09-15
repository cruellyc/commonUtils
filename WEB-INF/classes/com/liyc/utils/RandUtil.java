package com.liyc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandUtil {
	private static Random R = new Random();
	/**
	 * 产生随机字符串
	 * @param len
	 * @return
	 */
	public static String getRandStr(int l){
		if(l<=0) l = R.nextInt(10) + 5;
		byte[] s = new byte[l];
		s[0] = (byte)(65 + R.nextInt(26));
		for(int i=1; i<l; i++){
			s[i] = (byte)(97 + R.nextInt(26));
			
		}
		return new String(s);
	}
	/**
	 * 产生随机数字串
	 * @param l
	 * @return
	 */
	public static String getRandNum(int l){
		if(l<=0) l = R.nextInt(10) + 5;
		byte[] s = new byte[l];
		for(int i=0; i<l; i++){
			s[i] = (byte)(0x30 + R.nextInt(10));
			
		}
		return new String(s);
	}
	/**
	 * 产生以时间序号
	 * @return
	 */
	public static String genTimeSn(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return df.format(new Date());
	}
}
