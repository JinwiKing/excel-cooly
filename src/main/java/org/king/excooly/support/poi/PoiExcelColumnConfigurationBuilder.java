package org.king.excooly.support.poi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.king.excooly.ExcelCell;
import org.king.excooly.ExcelColumnEnum;
import org.king.excooly.ExcelDateCell;
import org.king.excooly.ExcelEnumCell;

public class PoiExcelColumnConfigurationBuilder {

	public static ExcelColumnConfiguration buildFrom(AccessibleObject ao) {
		ExcelColumnConfiguration cfg = null;
		Object anno = null;
		if((anno = ao.getAnnotation(ExcelDateCell.class)) != null) {
			ExcelDateCell column = (ExcelDateCell) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.requiredForDeserializing = column.requiredForDeserializing();
			cfg.columnIndex = column.columnIndex();
			cfg.columnName = column.columnName();
			cfg.name = column.name();
			cfg.matchPattern = column.matchPattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.requiredForSerializing = column.requiredForSerializing();
			cfg.order = column.order();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			
			cfg.isDateCell = true;
			String dateFormat = column.dateFormat();
			dateFormat = dateFormat == null ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
			cfg.dateFormat = dateFormat;
		}else if((anno = ao.getAnnotation(ExcelEnumCell.class)) != null) {
			ExcelEnumCell column = (ExcelEnumCell) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.requiredForDeserializing = column.requiredForDeserializing();
			cfg.columnIndex = column.columnIndex();
			cfg.columnName = column.columnName();
			cfg.name = column.name();
			cfg.matchPattern = column.matchPattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.requiredForSerializing = column.requiredForSerializing();
			cfg.order = column.order();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			
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
		}else if((anno = ao.getAnnotation(ExcelCell.class)) != null) {
			ExcelCell column = (ExcelCell) anno;
			
			cfg = new ExcelColumnConfiguration();
			cfg.accessibleObject = ao;
			cfg.requiredForDeserializing = column.requiredForDeserializing();
			cfg.columnIndex = column.columnIndex();
			cfg.columnName = column.columnName();
			cfg.name = column.name();
			cfg.matchPattern = column.matchPattern();
			cfg.deserializerType = column.deserializer();
			cfg.valueSetterType = column.setter();
			cfg.requiredForSerializing = column.requiredForSerializing();
			cfg.order = column.order();
			cfg.valueGetterType = column.getter();
			cfg.serializerType = column.serializer();
			cfg.concreteType = column.concreteType();
			
			if(ao instanceof Field) {
				Field field = (Field) ao;
				cfg.isContainer = Collection.class.isAssignableFrom(field.getType());
				boolean isArray = field.getType().isArray();
				cfg.isArray = isArray;
				if(isArray) cfg.concreteType = field.getType().getComponentType();
			}
		}
		return cfg;
	}
}
