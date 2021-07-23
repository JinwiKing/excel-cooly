package org.king.excooly.support.poi.l315b3;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.AbstractValueResolver;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 负责将Excel值转为Byte或byte类型以及将Byte或byte类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class ByteResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Byte.parseByte(propVal);
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return Byte.parseByte(celValStr);
			else return null;
		} 
		
		if(cell == null) return null;
		Byte v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_STRING: v = Byte.parseByte(cell.getStringCellValue()); break;
			case Cell.CELL_TYPE_NUMERIC: v = (byte) cell.getNumericCellValue(); break;
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to byte");
		}
		return v;
	}
	
	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Byte obj = (Byte) serializationParam.getJavaValue();
		if(obj == null) return null;
		return String.valueOf(obj);
	}
}
