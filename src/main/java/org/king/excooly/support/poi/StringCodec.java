package org.king.excooly.support.poi;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excooly.ExcelUtils;
import org.king.excooly.support.AbstractExcelValueDeserializer;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.PropertyValueSerializer;

/**
 * 负责将Excel值转为String类型以及将String类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
class StringCodec extends AbstractExcelValueDeserializer implements PropertyValueSerializer {

	@Override
	public Object innerDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cell();
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum) {
			Map<String, String> excelPropertyMap = configuration.excelPropertyMap;
			String celValStr = ExcelUtils.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return propVal;
			else if((propVal = configuration.defaultPropertyVal) != null) return propVal;
			else return null;
		} 
		
		if (cell == null) return null;
		String v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_BOOLEAN: v = String.valueOf(cell.getBooleanCellValue()); break;
			case Cell.CELL_TYPE_FORMULA: {
				try {
					v = cell.getStringCellValue();
				} catch (Exception e) {
					v = cell.getCellFormula();
					byte ecc = cell.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: v = new BigDecimal(cell.getNumericCellValue()).toString(); break;
			case Cell.CELL_TYPE_STRING: v = cell.getStringCellValue(); break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to string");
		}
		
		if (configuration.isAccountColumn) {
			// 专门处理工号列
			int inx = v.indexOf(".");
			if(inx >= 0) v = v.substring(0, inx);
		}
		
		return v;
	}

	@Override
	public void serialize(PropertyValueSerializationParameter serializationParam) {
		if(serializationParam.javaValue == null) return;
		
		String obj = (String) serializationParam.javaValue;
		ExcelColumnConfiguration configuration = serializationParam.columnConfiguration;
		if(configuration.isEnum && obj != null) {
			Map<String, String> propertyExcelMap = configuration.propertyExcelMap;
			String propVal = obj, cellValStr = propertyExcelMap.get(propVal);
			if(cellValStr == null && configuration.defaultExcelVal != null) cellValStr = configuration.defaultExcelVal;
			serializationParam.cell.setCellValue(cellValStr);
		}else serializationParam.cell.setCellValue(obj);
	}
}
