package com.liyc.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jxl.Sheet;
import jxl.Workbook;

/**
 * 导入Excel工具类
 *
 */
public class ExcelImportUtil {

	/**
	 * 类型转换
	 * 
	 * @param type
	 * @param param
	 * @return
	 */
	public static Object typeConversion(Class<?> type, String param) {
		if (type.getName().endsWith("byte") || type.getName().endsWith("Byte")) {
			return Byte.parseByte(param);
		}
		if (type.getName().endsWith("short") || type.getName().endsWith("Short")) {
			return Short.parseShort(param);
		}
		if (type.getName().endsWith("int") || type.getName().endsWith("Integer")) {
			return Integer.parseInt(param);
		}
		if (type.getName().endsWith("long") || type.getName().endsWith("Long")) {
			return Long.parseLong(param);
		}
		if (type.getName().endsWith("float") || type.getName().endsWith("Float")) {
			return Float.parseFloat(param);
		}
		if (type.getName().endsWith("double") || type.getName().endsWith("Double")) {
			return Double.parseDouble(param);
		}
		if (type.getName().endsWith("boolean") || type.getName().endsWith("Boolean")) {
			return Boolean.parseBoolean(param);
		}
		if (type.getName().endsWith("String")) {
			return param;
		}
		if (type.getName().endsWith("long") || type.getName().endsWith("Long")) {
			return Long.parseLong(param);
		}
		return null;
	}

	/**
	 * 是否对应set method
	 * 
	 * @param m
	 * @param name
	 * @return
	 */
	public static boolean equalSetMethod(Method m, String name) {
		String s = m.getName().substring(3);
		if (s.equalsIgnoreCase(name))
			return true;
		else
			return false;
	}

	/**
	 * 反射获取bean的set method
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Method> loadMethod(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		List<Method> res = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.getName().startsWith("set")) {
				res.add(m);
			}
		}
		return res;
	}

	/**
	 * 根据全类名返回一个该类对象
	 * 
	 * @param className
	 * @return
	 */
	public static Object loadClassByClassName(String className) {
		Object object = null;
		try {
			Class<?> c = Class.forName(className);
			object = c.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 把Excel中的数据返回成一个List
	 * 
	 * @param params
	 *            excel中的列名和bean的字段对应关系
	 * @param wb
	 *            Excel表格
	 * @param className
	 *            Excel中一行对应的bean
	 * @return
	 */
	public static List<?> getAllByExcel(Map<String, String> params, Workbook wb, String className, Logger logger) {
		List<Object> list = new ArrayList<Object>();
		List<String> paramNames = new ArrayList<String>();
		try {
			Sheet rs = wb.getSheet(0);
			int clos = rs.getColumns();// 得到所有的列
			int rows = rs.getRows();// 得到所有的行
			for (int i = 0; i < clos; i++) {
				paramNames.add(rs.getCell(i, 0).getContents());
			}
			for (int i = 1; i < rows; i++) {
				Object obj = loadClassByClassName(className);
				List<Method> methods = loadMethod(obj);
				boolean blank = false;
				for (int j = 0; j < clos; j++) {
					String param = rs.getCell(j, i).getContents();
					if (param == null || "".equals(param)) {
						blank = true;
						break;
					}
					logger.debug(paramNames.get(j) + ":" + param);
					if (params.get(paramNames.get(j)) != null) {
						for (Method m : methods) {
							if (equalSetMethod(m, params.get(paramNames.get(j)))) {
								Class<?> type = m.getParameterTypes()[0];
								m.invoke(obj, typeConversion(type, param));
							}
						}
					}
				}
				if (!blank) {
					list.add(obj);
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

}
