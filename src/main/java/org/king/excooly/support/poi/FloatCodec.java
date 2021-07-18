package org.king.excooly.support.poi;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.common.AbstractExcelValueDeserializer;
import org.king.excooly.support.common.ExcelUtils;

/**
 * 负责将Excel值转为Float或float类型以及将Float或float类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
class FloatCodec extends AbstractExcelValueDeserializer implements PropertyValueSerializer {

	@Override
	public Object innerDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum) {
			Map<String, String> excelPropertyMap = configuration.excelPropertyMap;
			String celValStr = ExcelUtils.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Float.parseFloat(propVal);
			else if((propVal = configuration.defaultPropertyVal) != null) return Float.parseFloat(propVal);
			else return null;
		} 
		
		if (cell == null) return null;
		Float v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_STRING: v = Float.parseFloat(cell.getStringCellValue()); break;
			case Cell.CELL_TYPE_NUMERIC: v = (float) cell.getNumericCellValue(); break;
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to float");
		}
		return v;
	}

	@Override
	public void serialize(PropertyValueSerializationParameter serializationParam) {
		if(serializationParam.javaValue == null) return;
		
		Float obj = (Float) serializationParam.javaValue;
		ExcelColumnConfiguration configuration = serializationParam.columnConfiguration;
		if(configuration.isEnum && obj != null) {
			Map<String, String> propertyExcelMap = configuration.propertyExcelMap;
			String propVal = String.valueOf(obj), cellValStr = propertyExcelMap.get(propVal);
			if(cellValStr == null && configuration.defaultExcelVal != null) cellValStr = configuration.defaultExcelVal;
			serializationParam.cell.setCellValue(cellValStr);
		}else serializationParam.cell.setCellValue(obj);
	}
}
