package com.liyc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * 解析excel 工具类
 * 
 * @author .
 *
 */
@SuppressWarnings("rawtypes")
public class ParseExcelUtil {

	public HSSFWorkbook workBook;
	public HSSFSheet sheet;
	public ParseXMLUtil parseXmlUtil;
	public StringBuffer errorString;

	/** 当前实体类的code **/
	public String curEntityCode;
	/** 表头map对象：key:entityCode, value:headMap(index,headTitle) **/
	public Map curEntityHeadMap;

	/** 存放每一行的数据 **/
	public List listDatas;

	public ParseExcelUtil(InputStream excelInputStream, InputStream xmlInputStream) {
		try {
			workBook = new HSSFWorkbook(excelInputStream);
			parseXmlUtil = new ParseXMLUtil(xmlInputStream);
			errorString = new StringBuffer();
			readExcelData();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 开始从excel读取数据 **/
	public void readExcelData() {
		int sheetSize = workBook.getNumberOfSheets();
		for (int i = 0; i < sheetSize; i++) {
			sheet = workBook.getSheetAt(i);
			String entityName = workBook.getSheetName(i);
			readSheetData(sheet, entityName);
		}

	}

	/** 读每个sheet页的数据 **/
	public void readSheetData(HSSFSheet sheet, String entityName) {
		int rowNumbers = sheet.getPhysicalNumberOfRows();
		Map ent = (Map) parseXmlUtil.getEntityMap().get(entityName);
		this.setCurEntityCode((String) ent.get("code"));
		if (rowNumbers == 0) {
			errorString.append(ParseConstans.ERROR_EXCEL_NULL);
		}
		List colList = (List) parseXmlUtil.getColumnListMap().get(entityName);
		int xmlRowNum = colList.size();
		HSSFRow excelRow = sheet.getRow(0);
		int excelFirstRow = excelRow.getFirstCellNum();
		int excelLastRow = excelRow.getLastCellNum();
		if (xmlRowNum != (excelLastRow - excelFirstRow)) {
			errorString.append(ParseConstans.ERROR_EXCEL_COLUMN_NOT_EQUAL);
		} else {
			readSheetHeadData(sheet);
			readSheetColumnData(sheet, entityName);
		}
	}

	/** 读取sheet页中的表头信息 **/
	@SuppressWarnings({ "unchecked", "static-access" })
	public void readSheetHeadData(HSSFSheet sheet) {
		Map headMap = new HashMap();
		curEntityHeadMap = new HashMap();
		HSSFRow excelheadRow = sheet.getRow(0);
		int excelLastRow = excelheadRow.getLastCellNum();
		String headTitle = "";
		for (int i = 0; i < excelLastRow; i++) {
			HSSFCell cell = excelheadRow.getCell(i);
			headTitle = this.getStringCellValue(cell);
			headMap.put(i, headTitle);
		}
		curEntityHeadMap.put(this.getCurEntityCode(), headMap);
	}

	/** 读取sheet页里面的数据 **/
	@SuppressWarnings({ "unchecked", "static-access" })
	public void readSheetColumnData(HSSFSheet sheet, String entityName) {
		HSSFRow excelheadRow = sheet.getRow(0);
		int excelLastcell = excelheadRow.getLastCellNum(); // excel总列数
		int excelRowNum = sheet.getLastRowNum(); // excel总行数
		Map headMap = (Map) this.getCurEntityHeadMap().get(this.getCurEntityCode());
		Map colMap = parseXmlUtil.getColumnMap();
		listDatas = new ArrayList();
		for (int i = 1; i < excelRowNum + 1; i++) {// 行循环
			HSSFRow columnRow = sheet.getRow(i);
			if (columnRow != null && columnRow.getCell(0) != null) {
				Map curRowCellMap = new HashMap();
				for (int j = 0; j < excelLastcell; j++) { // 列循环
					String headTitle = headMap.get(j).toString().trim();
					Map curColMap = (Map) colMap.get(entityName + "_" + headTitle);
					String curColCode = (String) curColMap.get("code");
					String curColType = (String) curColMap.get("type");
					HSSFCell colCell = columnRow.getCell(j);
					String cellValue = "";
					if (colCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						if (DateUtil.isCellDateFormatted(colCell)) {// 判断单元格是否属于日期格式
							cellValue = new SimpleDateFormat("yyyy-MM-dd").format(colCell.getDateCellValue())
									.toString();// java.util.Date类型
						} else {
							colCell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cellValue = this.getStringCellValue(colCell);
						}
					} else {
						cellValue = this.getStringCellValue(colCell);
					}
					/** 验证cell数据 **/
					boolean flag = validateCellData(i + 1, j + 1, cellValue, entityName, headTitle, curColMap);
					if (flag) {
						if (curColType.equals("int")) {
							int intVal = 0;
							if (!(cellValue == null || cellValue.equals(""))) {
								intVal = Integer.valueOf(cellValue);
							}
							curRowCellMap.put(curColCode, intVal);
						} else if (curColType.equals("double")) {
							double doubleVal = 0;
							if (!(cellValue == null || cellValue.equals(""))) {
								doubleVal = Double.parseDouble(cellValue);
							}
							curRowCellMap.put(curColCode, doubleVal);
						} else if (curColType.equals("Date")) {
							if (cellValue == null || cellValue.equals("")) {
								Date date = new Date();
								curRowCellMap.put(curColCode, date);
							} else {
								try {
									SimpleDateFormat sdf = new SimpleDateFormat((String) curColMap.get("format"));
									Date date = sdf.parse(cellValue);
									curRowCellMap.put(curColCode, date);
								} catch (Exception e) {

								}
							}
						} else {
							curRowCellMap.put(curColCode, cellValue);
						}
					}
				}
				listDatas.add(curRowCellMap);
			}
		}
	}

	/** 验证单元格数据 **/
	public boolean validateCellData(int curRow, int curCol, String cellValue, String entityName, String headName,
			Map curColMap) {
		String curColType = (String) curColMap.get("type");
		boolean flag = true;
		if (curColType.equals("int")) {
			if (ParseExcelUtil.isInteger(cellValue)) {
				String min = (String) curColMap.get("min");
				String max = (String) curColMap.get("max");
				if (min != null) {
					if (Double.parseDouble(cellValue) < Integer.valueOf(min)) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许小于" + min + "<br>");
						flag = false;
					}
				}
				if (max != null) {
					if (Integer.valueOf(cellValue) > Integer.valueOf(max)) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许大于" + max + "<br>");
						flag = false;
					}
				}
			} else {
				if (!(cellValue == null || cellValue.equals(""))) {
					errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "应该为整数<br>");
					flag = false;
				}
			}
		} else if (curColType.equals("double")) {
			if (ParseExcelUtil.isNumber(cellValue)) {
				String min = (String) curColMap.get("min");
				String max = (String) curColMap.get("max");
				if (min != null) {
					if (Double.parseDouble(cellValue) < Integer.valueOf(min)) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许小于" + min + "<br>");
						flag = false;
					}
				}
				if (max != null) {
					if (Integer.valueOf(cellValue) > Integer.valueOf(max)) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许大于" + max + "<br>");
						flag = false;
					}
				}
			} else {
				if (!(cellValue == null || cellValue.equals(""))) {
					errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "应该为数字<br>");
					flag = false;
				}
			}
		} else if (curColType.equals("String")) {
			String minLength = (String) curColMap.get("minLength");
			String maxLength = (String) curColMap.get("maxLength");
			if (minLength != null) {
				if (cellValue.length() < Integer.valueOf(minLength)) {
					errorString
							.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许小于" + minLength + "字符<br>");
					flag = false;
				}
			}
			if (maxLength != null) {
				if (cellValue.length() > Integer.valueOf(maxLength)) {
					errorString
							.append("第" + curRow + "行,第" + curCol + "列:" + headName + "不允许大于" + maxLength + "字符<br>");
					flag = false;
				}
			}
		} else if (curColType.equals("Date")) {
			String format = (String) curColMap.get("format");
			if (format != null) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(format);
					sdf.parse(cellValue);
				} catch (Exception e) {
					if (!(cellValue == null || cellValue.equals(""))) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + headName + "格式不对<br>");
						flag = false;
					}
				}
			}
		}

		// 规则验证
		List rulList = (List) parseXmlUtil.getColumnRulesMap().get(entityName + "_" + headName);
		if (rulList != null && rulList.size() > 0) {
			for (int i = 0; i < rulList.size(); i++) {
				Map rulM = (Map) rulList.get(i);
				String rulName = (String) rulM.get("name");
				String rulMsg = (String) rulM.get("message");
				if (rulName.equals(ParseConstans.RULE_NAME_NULLABLE)) {
					if (cellValue == null || cellValue.equals("")) {
						errorString.append("第" + curRow + "行,第" + curCol + "列:" + rulMsg + "<br>");
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获得单元格字符串
	 * 
	 * @throws UnSupportedCellTypeException
	 */
	public static String getStringCellValue(HSSFCell cell) {
		if (cell == null) {
			return null;
		} else {
			return cell.getStringCellValue().trim();
		}
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains(".")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	public String getCurEntityCode() {
		return curEntityCode;
	}

	public void setCurEntityCode(String curEntityCode) {
		this.curEntityCode = curEntityCode;
	}

	public Map getCurEntityHeadMap() {
		return curEntityHeadMap;
	}

	public void setCurEntityHeadMap(Map curEntityHeadMap) {
		this.curEntityHeadMap = curEntityHeadMap;
	}

	public ParseXMLUtil getParseXmlUtil() {
		return parseXmlUtil;
	}

	public void setParseXmlUtil(ParseXMLUtil parseXmlUtil) {
		this.parseXmlUtil = parseXmlUtil;
	}

	public List getListDatas() {
		return listDatas;
	}

	public void setListDatas(List listDatas) {
		this.listDatas = listDatas;
	}

	public StringBuffer getErrorString() {
		return errorString;
	}

	public void setErrorString(StringBuffer errorString) {
		this.errorString = errorString;
	}
}
