package org.king.excooly.support.poi;

import java.lang.reflect.AccessibleObject;
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
	
	AccessibleObject accessibleObject;
	
	Class<? extends ExcelCellValueDeserializer> deserializerType;
	ExcelCellValueDeserializer deserializer;
	Class<? extends JavaValueSetter> valueSetterType;
	JavaValueSetter valueSetter;
	
	Class<? extends JavaValueGetter> valueGetterType;
	JavaValueGetter valueGetter;
	Class<? extends PropertyValueSerializer> serializerType;
	PropertyValueSerializer serializer;

	// 日期类型表格参数（当注解为ExcelDateCell时生效）
	String dateFormat;
	
	// 基本参数
	int order;
	int width;
	String name;
	boolean ignoreSerialization;
	boolean ignoreDeserialization;
	
	String namePattern;
	boolean required;
	
	// 枚举类型表格参数（当注解为ExcelEnumColumn时生效）
	boolean isEnum = false;
	Map<String, String> propertyExcelMap;
	String defaultExcelVal;
	Map<String, String> excelPropertyMap;
	String defaultPropertyVal;
	
	// 特殊列-工号
	boolean isAccountColumn = false;

	/**
	 * 序列化到Excel表格时，属性对应表格顺序。值越大越靠后。
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * 序列化到Excel表格时，该属性对应的表格宽度。
	 */
	public int getWidth() {
		return width;
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

	public String getNamePattern() {
		return namePattern;
	}

	public boolean isRequired() {
		return required;
	}

	public Class<? extends ExcelCellValueDeserializer> getDeserializer() {
		return deserializerType;
	}

	public Class<? extends JavaValueSetter> getSetter() {
		return valueSetterType;
	}
}
