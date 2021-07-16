package org.king.excooly.support.poi;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 对象属性值序列化时，序列化器的入参
 * @author wangjw5
 */
public class PropertyValueSerializationParameter {
	/**
	 * 即将序列化的属性的值
	 */
	Object javaValue;
	/**
	 * 目标单元格
	 */
	Cell cell;
	/**
	 * 当前excel表格内的CellStyle名字对CellStyle的映射。
	 * 用于暂存序列化时产生的CellStyle，减少重复new
	 */
	Map<String, CellStyle> nameCellStyleMap = new HashMap<>();
	/**
	 * 单元格所在的excel
	 */
	Workbook wb;
	/**
	 * 读取到的即将序列化的属性的注解
	 */
	ExcelColumnConfiguration columnConfiguration;
	
	public Object getJavaValue() {
		return javaValue;
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public Map<String, CellStyle> getNameCellStyleMap() {
		return nameCellStyleMap;
	}
	
	public Workbook getWb() {
		return wb;
	}
	
	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}
}
