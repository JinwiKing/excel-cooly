package org.king.excooly.support.poi;

import java.util.Map;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.support.ExcelValueDeserializerParameter;

public class PoiExcelValueDeserializerParameter implements ExcelValueDeserializerParameter {
	int rowIdx;
	Object[] cells;
	FocusingExcelColumn readingColumn;
	Map<Class<?>, ExcelCellValueDeserializer> deserializes;
	Class<?> targetJavaType;
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
}
