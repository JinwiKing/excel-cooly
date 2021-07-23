package org.king.excooly.support.poi;

public class ReadingExcelColumn {
	ExcelColumnConfiguration columnConfiguration;
	int colIdx = -1;
	String colName;
	boolean isCascaded;
	ReadingExcelColumnCollection cascadeCollection;

	public ReadingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
	}

	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}

	public int getColIdx() {
		return colIdx;
	}

	public String getColName() {
		return colName;
	}

	public boolean isCascaded() {
		return isCascaded;
	}

	public ReadingExcelColumnCollection getCascadeCollection() {
		return cascadeCollection;
	}
}
