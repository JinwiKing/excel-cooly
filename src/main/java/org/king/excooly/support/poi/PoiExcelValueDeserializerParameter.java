package org.king.excooly.support.poi;

import org.king.excooly.support.ExcelValueDeserializerParameter;

public class PoiExcelValueDeserializerParameter implements ExcelValueDeserializerParameter {
	int rowIdx;
	Object cell;
	ReadingExcelColumn readingColumn;
	@Override
	public int rowIdx() {
		return rowIdx;
	}
	@Override
	public int colIdx() {
		return readingColumn.colIdx;
	}
	@Override
	public String colName() {
		return readingColumn.colName;
	}
	@Override
	public ExcelColumnConfiguration configuration() {
		return readingColumn.getConfiguration();
	}
	@Override
	public Object cell() {
		return cell;
	}
}
