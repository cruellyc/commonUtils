package com.liyc.utils;
/**
 * 浮点型格式化
 *
 */
public class FloatFamUtil {
	/**
	 * 浮点型格式化1位小数
	 * @param num
	 * @return
	 */
	public static float famFloat1(float num){
		float n=(float) (Math.round(num*10)/10.0);
		return n;
	}
	/**
	 * 浮点型格式化两位小数
	 * @param num
	 * @return
	 */
	public static float famFloat2(float num){
		float n=(float) (Math.round(num*100)/100.0);
		return n;
	}
	/**
	 * double格式化1位小数
	 * @param num
	 * @return
	 */
	public static float famFloat1(double num){
		float n=(float) (Math.round(num*10)/10.0);
		return n;
	}
	/**
	 * double格式化两位小数
	 * @param num
	 * @return
	 */
	public static float famFloat2(double num){
		float n=(float) (Math.round(num*100)/100.0);
		return n;
	}
}
