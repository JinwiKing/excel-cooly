package org.king.excooly;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.support.poi.ExcelColumnConfiguration;

@ExcelTable
public class ExcelUtils {
	
	public static String getCellValueAsString(Cell c) {
		if (c == null) return null;
		String v = null;
		int ct = c.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_BOOLEAN: v = String.valueOf(c.getBooleanCellValue()); break;
			case Cell.CELL_TYPE_STRING: v = c.getStringCellValue(); break;
			case Cell.CELL_TYPE_FORMULA: {
				try {
					v = c.getStringCellValue();
				} catch (Exception e) {
					v = c.getCellFormula();
					byte ecc = c.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: v = String.valueOf(c.getNumericCellValue()); break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
		}
		return v;
	}
	
	public static Sheet getOrCreateSheet(Workbook wb, String sheetName) {
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null) sheet = wb.createSheet(sheetName);
		return sheet;
	}
	
	public static Row getOrCreateRow(Sheet sheet, int rowNo) {
		Row row = sheet.getRow(rowNo);
		if(row == null) row = sheet.createRow(rowNo);
		return row;
	}
	
	public static Cell getOrCreateCell(Row row, int colNo) {
		Cell cell = row.getCell(colNo);
		if(cell == null) cell = row.createCell(colNo);
		return cell;
	}
	
	static ExcelColumnConfiguration getColumnConfiguration(AccessibleObject acObj) {
		ExcelColumnConfiguration cfg = null;
		Object anno = null;
		if((anno = acObj.getAnnotation(ExcelDateColumn.class)) != null) {
			ExcelDateColumn column = (ExcelDateColumn) anno;
			
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
			
			String dateFormat = column.dateFormat();
			dateFormat = dateFormat == null ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
			cfg.dateFormat = dateFormat;
		}else if((anno = acObj.getAnnotation(ExcelEnumColumn.class)) != null) {
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
		}else if((anno = acObj.getAnnotation(ExcelCcafAccountColumn.class)) != null) {
			ExcelCcafAccountColumn column = (ExcelCcafAccountColumn) anno;
			
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
			
			cfg.isAccountColumn = true;
		}else if((anno = acObj.getAnnotation(ExcelColumn.class)) != null) {
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
	
	static ExcelTable getOrUseDefaultExcelTableAnnotation(Class<?> dataType) {
		ExcelTable excelTable = dataType.getAnnotation(ExcelTable.class);
		if(excelTable == null) excelTable = ExcelWriter.class.getDeclaredAnnotation(ExcelTable.class);
		return excelTable;
	}
}
