package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excool.ExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.CellValueResolver;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 负责将Excel值转为String类型以及将String类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class StringResolver implements CellValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return propVal;
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return propVal;
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
		
		return v;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		return (String) serializationParam.getJavaValue();
	}
}
