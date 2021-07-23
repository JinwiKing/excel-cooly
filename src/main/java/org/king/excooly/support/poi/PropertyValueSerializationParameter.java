package org.king.excooly.support.poi;

/**
 * 对象属性值序列化时，序列化器的入参
 * @author king
 */
public class PropertyValueSerializationParameter {
	/**
	 * 即将序列化的属性的值
	 */
	Object javaValue;
	/**
	 * 读取到的即将序列化的属性的注解
	 */
	ExcelColumnConfiguration columnConfiguration;
	
	public Object getJavaValue() {
		return javaValue;
	}
	
	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}
}
