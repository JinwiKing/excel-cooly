package org.king.excooly;

import java.util.Map;

public interface ExcelCoolyConfiguration {
	
	/**
	 * 获取单元格值反序列化器
	 */
	Map<Class<?>, ExcelCellValueDeserializer> getCellValueDeserializers();
}
