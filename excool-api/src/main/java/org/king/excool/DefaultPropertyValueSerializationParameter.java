package org.king.excool;

/**
 * 对象属性值序列化时，序列化器的入参
 * @author king
 */
public class DefaultPropertyValueSerializationParameter implements PropertyValueSerializationParameter {
	/**
	 * 即将序列化的属性的值
	 */
	Object javaValue;
	/**
	 * 读取到的即将序列化的属性的注解
	 */
	ExcelColumnConfiguration columnConfiguration;

	@Override
	public Object getJavaValue() {
		return javaValue;
	}

	public void setJavaValue(Object javaValue) {
		this.javaValue = javaValue;
	}

	@Override
	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}

	public void setColumnConfiguration(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
	}
}
