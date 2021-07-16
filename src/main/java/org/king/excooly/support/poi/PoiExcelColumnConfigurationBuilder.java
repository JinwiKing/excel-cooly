package org.king.excooly.support.poi;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Map;

import org.king.excooly.ExcelColumn;
import org.king.excooly.ExcelColumnEnum;
import org.king.excooly.ExcelDateColumn;
import org.king.excooly.ExcelEnumColumn;

public class PoiExcelColumnConfigurationBuilder {

	public static ExcelColumnConfiguration buildFrom(AccessibleObject ao) {
		ExcelColumnConfiguration cfg = null;
		Object anno = null;
		if((anno = ao.getAnnotation(ExcelDateColumn.class)) != null) {
			ExcelDateColumn column = (ExcelDateColumn) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.accessibleObject = ao;
			cfg.order = column.order();
			cfg.width = column.width();
			cfg.name = column.name();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			cfg.namePattern = column.namePattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.required = column.required();
			cfg.ignoreSerialization = column.ignoreSerialization();
			cfg.ignoreDeserialization = column.ignoreDeserialization();
			
			String dateFormat = column.dateFormat();
			dateFormat = dateFormat == null ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
			cfg.dateFormat = dateFormat;
		}else if((anno = ao.getAnnotation(ExcelEnumColumn.class)) != null) {
			ExcelEnumColumn column = (ExcelEnumColumn) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.order = column.order();
			cfg.width = column.width();
			cfg.name = column.name();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			cfg.namePattern = column.namePattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.required = column.required();
			cfg.ignoreSerialization = column.ignoreSerialization();
			cfg.ignoreDeserialization = column.ignoreDeserialization();
			
			cfg.isEnum = true;
			ExcelColumnEnum[] enums = column.enums();
			Map<String, String> propertyExcelMap = new HashMap<>(), excelPropertyMap = new HashMap<>();
			cfg.propertyExcelMap = propertyExcelMap;
			cfg.excelPropertyMap = excelPropertyMap;
			for(ExcelColumnEnum oneEnum : enums) {
				String java = oneEnum.javaVal(), excel = oneEnum.excelVal();
				propertyExcelMap.put(java, excel);
				excelPropertyMap.put(excel, java);
			}
			String defaultVal = column.defaultPropertyVal();
			defaultVal = defaultVal.equals("") && !column.defaultPropertyValAsEmptyString() ? null : defaultVal;
			cfg.defaultPropertyVal = defaultVal;
			defaultVal = column.defaultExcelVal();
			defaultVal = defaultVal.equals("") && !column.defaultExcelValAsEmptyString() ? null : defaultVal;
			cfg.defaultExcelVal = defaultVal;
		}else if((anno = ao.getAnnotation(ExcelColumn.class)) != null) {
			ExcelColumn column = (ExcelColumn) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.order = column.order();
			cfg.width = column.width();
			cfg.name = column.name();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			cfg.namePattern = column.namePattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.required = column.required();
			cfg.ignoreSerialization = column.ignoreSerialization();
			cfg.ignoreDeserialization = column.ignoreDeserialization();
		}
		return cfg;
	}
}
