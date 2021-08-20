package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excool.ExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.CellValueResolver;

import java.util.Map;

/**
 * 负责将Excel值转为Short或short类型以及将Short或short类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class ShortResolver implements CellValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Short.parseShort(propVal);
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return Short.parseShort(propVal);
			else return null;
		} 
		
		if (cell == null) return null;
		Short v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_STRING: v = Short.parseShort(cell.getStringCellValue()); break;
			case Cell.CELL_TYPE_NUMERIC: v = (short) cell.getNumericCellValue(); break;
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to short");
		}
		return v;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Short obj = (Short) serializationParam.getJavaValue();
		if(obj == null) return null;
		return String.valueOf(obj);
	}
}
