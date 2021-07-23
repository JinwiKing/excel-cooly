package org.king.excooly.support.poi.ge315b3;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.AbstractValueResolver;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 负责将Excel值转为Double或double类型以及将Double或double类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class DoubleResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return Double.parseDouble(propVal);
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return Double.parseDouble(propVal);
			else return null;
		} 
		
		if (cell == null) return null;
		Double v = null;
		CellType ct = cell.getCellType();
		switch (ct) {
			case STRING: v = Double.parseDouble(cell.getStringCellValue()); break;
			case NUMERIC: v = cell.getNumericCellValue(); break;
			case BLANK: v = null; break;
			case ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to double");
		}
		return v;
	}
	
	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Double obj = (Double) serializationParam.getJavaValue();
		if(obj == null) return null;
		return String.valueOf(obj);
	}
}
