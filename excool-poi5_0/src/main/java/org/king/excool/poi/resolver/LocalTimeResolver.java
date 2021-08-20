package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excool.DefaultExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.AbstractValueResolver;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		DefaultExcelColumnConfiguration configuration = deserializerParam.configuration();
		if (cell == null) return null;
		String v = null;
		CellType ct = cell.getCellType();
		switch (ct) {
			case BLANK: v = null; break;
			case STRING: v = cell.getStringCellValue(); break;
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
			case NUMERIC: v = String.valueOf(cell.getNumericCellValue()); break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to local date");
		}
		
		return LocalTime.parse(v, DateTimeFormatter.ofPattern(configuration.getDateFormat()));
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		LocalTime lt = (LocalTime) serializationParam.getJavaValue();
		return lt.format(DateTimeFormatter.ofPattern(serializationParam.getColumnConfiguration().getDateFormat()));
	}

}
