package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.king.excool.DefaultExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.AbstractValueResolver;

import java.util.Map;

/**
 * 负责将Excel值转为Long或long类型以及将Long或long类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class LongResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		DefaultExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Long.parseLong(propVal);
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return Long.parseLong(propVal);
			else return null;
		} 
		
		if (cell == null) return null;
		Long v = null;
		CellType ct = cell.getCellType();
		switch (ct) {
			case STRING: v = Long.parseLong(cell.getStringCellValue()); break;
			case NUMERIC: v = (long) cell.getNumericCellValue(); break;
			case BLANK: v = null; break;
			case ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to long");
		}
		return v;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Long obj = (Long) serializationParam.getJavaValue();
		if(obj == null) return null;
		return String.valueOf(obj);
	}
}
