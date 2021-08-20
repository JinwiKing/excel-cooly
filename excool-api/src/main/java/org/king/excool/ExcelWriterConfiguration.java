package org.king.excool;

import java.util.Set;

/**
 * @version 1.0
 * */
public interface ExcelWriterConfiguration {
	
	/**
	 * 返回写入文件的列名
	 * @since 1.0
	 * @author wangjw5
	 * */
	Set<String> getColumnNames(String sheetName);
	
	/**
	 * 返回标头行所在的行数（从0开始算）。
	 * @return -1 不设置标头行；>=0所在的行数
	 * */
	int getTitleRowIndex(String sheetName);
	
	/**
	 * 给定列名，返回列所在的列数（从0开始算）。
	 * @since 1.0
	 * @author wangjw5
	 * */
	int getColumnIndex(String sheetName, String columnName);
	
	/**
	 * 添加Object时，将从这里返回需要写入文件的字段名
	 * @since 1.0
	 * @author wangjw5
	 * */
	Set<String> getFieldNames(String sheetName);
	
	/**
	 * 给定Object字段名返回列名
	 * @since 1.0
	 * @author wangjw5
	 * */
	String getColumnNameByFieldName(String sheetName, String fieldName);
	
	/**
	 * 读取Object的值时，将回调改方法确认最终值，允许用户调整数据值。
	 * @since 1.0
	 * @author wangjw5
	 * */
	Object processObjectValue(String sheetName, String fieldName, Object srcValue);
	
	/**
	 * 获取sheet的位置权重，权重越大，顺序越靠前。
	 * @since 1.0
	 * @author wangjw5
	 * */
	int getSheetArrangementWeight(String sheetName);
}
