package org.king.excooly.support.poi;

import java.util.List;

/**
 * 封装解析的注解和注解对象的属性或方法
 * @author king
 */
public class FocusingExcelColumn {
	ExcelColumnConfiguration configuration;
	int colIdx = -1;
	String colName;
	boolean isEmbedded;
	List<FocusingExcelColumn> embeddedExcelColumns;
	
	public FocusingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		this.configuration = columnConfiguration;
	}

	public ExcelColumnConfiguration getConfiguration() {
		return configuration;
	}

	public int getColIdx() {
		return colIdx;
	}

	public String getColName() {
		return colName;
	}

	public boolean isEmbedded() {
		return isEmbedded;
	}

	public List<FocusingExcelColumn> getEmbeddedExcelColumns() {
		return embeddedExcelColumns;
	}
}
