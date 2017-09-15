package com.liyc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Excel工具导出
 */
public class ExcelExportUtil {
	public static final int START_ROW = 2;
	public static final int START_CELL = 0;

	public static boolean equalGetMethod(Method m, String name) {
		String s = m.getName().substring(3);
		if (s.equalsIgnoreCase(name))
			return true;
		else
			return false;
	}

	/**
	 * Excel导出
	 * 
	 * @param propertiesName
	 *            配置文件名，例"seedreport.properties"
	 * @param list
	 *            导出所需数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static void exportData(HttpServletRequest req,
			HttpServletResponse resp, String propertiesName, List list,
			String fileName) {
		ExcelProperties properties = null;
		try {
			properties = new ExcelProperties();
			properties.load(new InputStreamReader(ExcelExportUtil.class
					.getResourceAsStream(propertiesName), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		List<String> title = new ArrayList<String>();
		List<String> columnList = new ArrayList<String>();
		for (Object t : properties.keySet()) {
			title.add(properties.getProperty(t.toString()));
			columnList.add(t.toString());
		}
		ServletOutputStream out = null;
		try {
			out = resp.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("application/octet-stream");
		String str = null;
		try {
			str = new String(fileName.getBytes("gbk"), "ISO8859-1");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}
		resp.setHeader("Content-disposition", "attachment; filename=" + str
				+ ".xls");
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(out);

			WritableSheet sheet = workbook.createSheet("sheet", 0);

			WritableFont font1 = new WritableFont(WritableFont.TIMES, 10,
					WritableFont.NO_BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			List<Method> methods = new ArrayList<Method>();
			for (int i = 0; i < title.size(); i++) {
				sheet.addCell(new Label(i, 0, title.get(i), format1));
			}
			if (list == null) {
				workbook.write();
				workbook.close();
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				int row = i + 1;
				if (i == 0) {
					Object obj = list.get(i);
					Method[] methods2 = obj.getClass().getMethods();
					for (Method m : methods2) {
						if (m.getName().startsWith("get")
								&& !m.getName().equals("getClass")) {
							methods.add(m);
						}
					}
				}
				int k = 0;
				for (String c : columnList) {
					for (Method m : methods) {
						if (equalGetMethod(m, c)) {
							Object o = null;
							;
							try {

								o = m.invoke(list.get(i));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							if (o == null)
								sheet.addCell(new Label(k++, row, "", format1));
							else
								sheet.addCell(new Label(k++, row, "" + o,
										format1));
							break;
						}
					}
				}
			}
			workbook.write();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return;
	}

	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(xls)$");
	}

	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(xlsx)$");
	}

	public static String getString(Cell cell) {
		String str = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			str = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			str = String.format("%.0f", cell.getNumericCellValue());
			break;
		}
		return str;
	}

	public static Integer getInteger(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			return (int) cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			String str = cell.getStringCellValue();
			if (str.equals(""))
				return null;
			return Integer.parseInt(str);
		}
		return null;
	}

	public static Double getDouble(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			String str = cell.getStringCellValue();
			if (str.equals(""))
				return null;
			return Double.parseDouble(str);
		}
		return null;
	}
	/**
	 * 获取当前日期，返回字符串格式
	 * 
	 * @return
	 */
	public static String getCurrtDoneTimeString() {
		String d="";
		try {
			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
			d=dateFm.format(date);
			return d;
		} catch (Exception e) {
			return d;
		}

	}
}
