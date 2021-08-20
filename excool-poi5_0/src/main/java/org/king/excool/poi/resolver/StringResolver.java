package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excool.DefaultExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.AbstractValueResolver;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 负责将Excel值转为String类型以及将String类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class StringResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		DefaultExcelColumnConfiguration configuration = deserializerParam.configuration();
		if(configuration.isEnum()) {
			Map<String, String> excelPropertyMap = configuration.getExcelPropertyMap();
			String celValStr = CellValueHelper.getCellValueAsString(cell), propVal = excelPropertyMap.get(celValStr);
			if(propVal != null) return propVal;
			else if((propVal = configuration.getDefaultPropertyVal()) != null) return propVal;
			else return null;
		} 
		
		if (cell == null) return null;
		String v = null;
		CellType ct = cell.getCellType();
		switch (ct) {
			case BLANK: v = null; break;
			case BOOLEAN: v = String.valueOf(cell.getBooleanCellValue()); break;
			case FORMULA: {
				try {
					v = cell.getStringCellValue();
				} catch (Exception e) {
					v = cell.getCellFormula();
					byte ecc = cell.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case NUMERIC: v = new BigDecimal(cell.getNumericCellValue()).toString(); break;
			case STRING: v = cell.getStringCellValue(); break;
			case ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to string");
		}
		
		return v;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		return (String) serializationParam.getJavaValue();
	}
}
