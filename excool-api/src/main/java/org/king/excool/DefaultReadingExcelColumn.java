package org.king.excool;

public class DefaultReadingExcelColumn implements ReadingExcelColumn {
	ExcelColumnConfiguration columnConfiguration;
	int colIdx = -1;
	String colName;
	boolean isCascaded;
	ReadingExcelColumnCollection cascadeCollection;

	public DefaultReadingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
	}

	@Override
	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}

	public void setColumnConfiguration(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
	}

	@Override
	public int getColIdx() {
		return colIdx;
	}

	public void setColIdx(int colIdx) {
		this.colIdx = colIdx;
	}

	@Override
	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	@Override
	public boolean isCascaded() {
		return isCascaded;
	}

	public void setCascaded(boolean cascaded) {
		isCascaded = cascaded;
	}

	@Override
	public ReadingExcelColumnCollection getCascadeCollection() {
		return cascadeCollection;
	}

	public void setCascadeCollection(ReadingExcelColumnCollection cascadeCollection) {
		this.cascadeCollection = cascadeCollection;
	}
}
