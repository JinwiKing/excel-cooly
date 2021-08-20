package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excool.ExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.CellValueResolver;

import java.util.Map;

/**
 * 负责将Excel值转为Float或float类型以及将Float或float类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class FloatResolver implements CellValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Float.parseFloat(propVal);
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return Float.parseFloat(propVal);
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
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Float obj = (Float) serializationParam.getJavaValue();
		if(obj == null) return null;
		return String.valueOf(obj);
	}
}
