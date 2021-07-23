package org.king.excooly.support.poi.ge315b3;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.AbstractValueResolver;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

public class LocalDateResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if (cell == null) return null;
		String v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_STRING: v = cell.getStringCellValue(); break;
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
			case Cell.CELL_TYPE_NUMERIC: v = String.valueOf(cell.getNumericCellValue()); break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to local date");
		}
		
		return LocalDate.parse(v, DateTimeFormatter.ofPattern(configuration.getDateFormat()));
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		LocalDate ld = (LocalDate) serializationParam.getJavaValue();
		return ld.format(DateTimeFormatter.ofPattern(serializationParam.getColumnConfiguration().getDateFormat()));
	}

}
