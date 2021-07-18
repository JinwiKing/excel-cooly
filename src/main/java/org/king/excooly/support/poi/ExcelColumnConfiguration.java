package org.king.excooly.support.poi;

import java.lang.reflect.AccessibleObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.PropertyValueSerializer;

/**
 * 解析ExcelColumn注解得到的配置信息
 * @author wangjw5
 */
public class ExcelColumnConfiguration {
	Class<?> dataType;
	
	AccessibleObject accessibleObject;
	
	boolean requiredForDeserializing;
	int columnIndex;
	String columnName;
	String name;
	String matchPattern;
	Class<? extends ExcelCellValueDeserializer> deserializerType;
	ExcelCellValueDeserializer deserializer;
	Class<? extends JavaValueSetter> valueSetterType;
	JavaValueSetter valueSetter;
	boolean requiredForSerializing;
	int order;
	Class<? extends JavaValueGetter> valueGetterType;
	JavaValueGetter valueGetter;
	Class<? extends PropertyValueSerializer> serializerType;
	PropertyValueSerializer serializer;
	
	boolean isContainer = false;
	boolean isArray = false;
	Class<?> concreteType;
	
	boolean isDateCell = false;
	// 日期类型表格参数（当注解为ExcelDateCell时生效）
	String dateFormat;
	
	// 枚举类型表格参数（当注解为ExcelEnumColumn时生效）
	boolean isEnum = false;
	Map<String, String> propertyExcelMap;
	String defaultExcelVal;
	Map<String, String> excelPropertyMap;
	String defaultPropertyVal;
	
	boolean isEmbedded = false;
	List<ExcelColumnConfiguration> embeddedConfigurations;
	
	/**
	 * 序列化到Excel表格时，属性对应表格顺序。值越大越靠后。
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * 对应Excel表格的列名（目前只在序列化时生效）
	 */
	public String getName() {
		return name;
	}

	/**
	 * 指定读取属性的获取器,使用该获取器获取属性值
	 */
	public Class<? extends JavaValueGetter> getGetter(){
		return valueGetterType;
	}

	/**
	 * 指定序列化器对属性值到excel单元格进行序列化
	 */
	public Class<? extends PropertyValueSerializer> getSerializer(){
		return serializerType;
	}

	/**
	 * 日期格式
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	public Class<? extends ExcelCellValueDeserializer> getDeserializer() {
		return deserializerType;
	}

	public Class<? extends JavaValueSetter> getSetter() {
		return valueSetterType;
	}

	public AccessibleObject getAccessibleObject() {
		return accessibleObject;
	}

	public boolean isRequiredForDeserializing() {
		return requiredForDeserializing;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getMatchPattern() {
		return matchPattern;
	}

	public Class<? extends ExcelCellValueDeserializer> getDeserializerType() {
		return deserializerType;
	}

	public Class<? extends JavaValueSetter> getValueSetterType() {
		return valueSetterType;
	}

	public JavaValueSetter getValueSetter() {
		return valueSetter;
	}

	public boolean isRequiredForSerializing() {
		return requiredForSerializing;
	}

	public Class<? extends JavaValueGetter> getValueGetterType() {
		return valueGetterType;
	}

	public JavaValueGetter getValueGetter() {
		return valueGetter;
	}

	public Class<? extends PropertyValueSerializer> getSerializerType() {
		return serializerType;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public Map<String, String> getPropertyExcelMap() {
		return propertyExcelMap;
	}

	public String getDefaultExcelVal() {
		return defaultExcelVal;
	}

	public Map<String, String> getExcelPropertyMap() {
		return excelPropertyMap;
	}

	public String getDefaultPropertyVal() {
		return defaultPropertyVal;
	}
}
