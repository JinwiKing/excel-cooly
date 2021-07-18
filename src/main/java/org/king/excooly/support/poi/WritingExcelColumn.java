package org.king.excooly.support.poi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.PropertyValueSerializer;

/**
 * 写入中的excel单元格
 * @author wangjw5
 */
public class WritingExcelColumn extends FocusingExcelColumn {
	/**
	 * 注解的如果是属性的话，可能会用JavaBean的Getter，当然，如果有的话；
	 * 或者，如果注解的是方法的话，该属性就是那个注解的方法。
	 */
	Method objctJavaBeanGetter;
	
	int order;
	int width;	// Excel的单元格宽度
	String usingColName;
	
	/**
	 * 使用的值读取器
	 */
	JavaValueGetter valueGetter;
	/**
	 * 使用的属性值序列化到Excel值的序列化器
	 */
	PropertyValueSerializer serializer;
	
	WritingExcelColumn(ExcelColumnConfiguration excelColumn, AccessibleObject annotatedObject, Method javaBeanGetter, JavaValueGetter valueGetter, PropertyValueSerializer serializer) {
		super(excelColumn);
		this.objctJavaBeanGetter = javaBeanGetter;
		this.order = excelColumn.order;
//		this.width = excelColumn.width * 256;
		this.usingColName = excelColumn.name;
		if(usingColName == null || usingColName.trim().length() <= 0) {
			if(annotatedObject instanceof Field) usingColName = ((Field) annotatedObject).getName();
			else if(annotatedObject instanceof Method) {
				usingColName = ((Method) annotatedObject).getName();
				if(usingColName.startsWith("get")) {
					usingColName = usingColName.substring(3);
					usingColName = usingColName.substring(0, 1).toLowerCase().concat(usingColName.substring(1));
				}else if(usingColName.startsWith("is")) {
					usingColName = usingColName.substring(2);
					usingColName = usingColName.substring(0, 1).toLowerCase().concat(usingColName.substring(1));
				}
			}
		}
		this.valueGetter = valueGetter;
		this.serializer = serializer;
	}

	public Method getObjctJavaBeanGetter() {
		return objctJavaBeanGetter;
	}
	
	public JavaValueGetter getValueGetter() {
		return valueGetter;
	}

	public PropertyValueSerializer getSerializer() {
		return serializer;
	}
}
