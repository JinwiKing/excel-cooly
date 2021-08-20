package org.king.excool;

import java.util.Map;

public class DefaultExcelValueDeserializerParameter implements ExcelValueDeserializerParameter {
	int rowIdx;
	Object[] cells;
	ReadingExcelColumn readingColumn;
	Map<Class<?>, ExcelCellValueDeserializer> deserializes;
	Class<?> targetJavaType;

	@Override
	public int rowIdx() {
		return rowIdx;
	}
	@Override
	public int colIdx() {
		return readingColumn.getColIdx();
	}
	@Override
	public String colName() {
		return readingColumn.getColName();
	}
	@Override
	public ExcelColumnConfiguration configuration() {
		return readingColumn.getColumnConfiguration();
	}
	@Override
	public Object[] cells() {
		return cells;
	}
	@Override
	public Map<Class<?>, ExcelCellValueDeserializer> deserializers() {
		return deserializes;
	}
	@Override
	public Class<?> targetJavaType() {
		return targetJavaType;
	}

	public void setRowIdx(int rowIdx) {
		this.rowIdx = rowIdx;
	}

	public void setCells(Object[] cells) {
		this.cells = cells;
	}

	public void setReadingColumn(ReadingExcelColumn readingColumn) {
		this.readingColumn = readingColumn;
	}

	public void setDeserializes(Map<Class<?>, ExcelCellValueDeserializer> deserializes) {
		this.deserializes = deserializes;
	}

	public void setTargetJavaType(Class<?> targetJavaType) {
		this.targetJavaType = targetJavaType;
	}
}
