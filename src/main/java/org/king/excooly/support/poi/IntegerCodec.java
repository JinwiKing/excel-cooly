package org.king.excooly.support.poi;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.common.AbstractExcelValueDeserializer;
import org.king.excooly.support.common.ExcelUtils;

/**
 * 负责将Excel值转为Integer或int类型以及将Integer或int类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
class IntegerCodec extends AbstractExcelValueDeserializer implements PropertyValueSerializer {

	@Override
	public Object innerDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum) {
			Map<String, String> excelPropertyMap = configuration.excelPropertyMap;
			String celValStr = ExcelUtils.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Integer.parseInt(propVal);
			else if((propVal = configuration.defaultPropertyVal) != null) return Integer.parseInt(propVal);
			else return null;
		} 
		
		if (cell == null) return null;
		Integer v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_STRING: v = Integer.parseInt(cell.getStringCellValue()); break;
			case Cell.CELL_TYPE_NUMERIC: v = (int) cell.getNumericCellValue(); break;
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to integer");
		}
		return v;
	}

	@Override
	public void serialize(PropertyValueSerializationParameter serializationParam) {
		if(serializationParam.javaValue == null) return;
		
		Integer obj = (Integer) serializationParam.javaValue;
		ExcelColumnConfiguration configuration = serializationParam.columnConfiguration;
		if(configuration.isEnum && obj != null) {
			Map<String, String> propertyExcelMap = configuration.propertyExcelMap;
			String propVal = String.valueOf(obj), cellValStr = propertyExcelMap.get(propVal);
			if(cellValStr == null && configuration.defaultExcelVal != null) cellValStr = configuration.defaultExcelVal;
			serializationParam.cell.setCellValue(cellValStr);
		}else serializationParam.cell.setCellValue(obj);
	}
}
