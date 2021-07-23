package org.king.excooly.support.poi.ge315b3;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excooly.support.poi.SimpleCellValueReader;

public class CellValueHelper implements SimpleCellValueReader {

	static String getCellValueAsString(Cell c) {
		if (c == null) return null;
		String v = null;
		CellType ct = c.getCellType();
		switch (ct) {
			case BLANK: v = null; break;
			case BOOLEAN: v = String.valueOf(c.getBooleanCellValue()); break;
			case STRING: v = c.getStringCellValue(); break;
			case FORMULA: {
				try {
					v = c.getStringCellValue();
				} catch (Exception e) {
					v = c.getCellFormula();
					byte ecc = c.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case NUMERIC: v = String.valueOf(c.getNumericCellValue()); break;
			case ERROR: v = null; break;
			default: return v;
		}
		return v;
	}

	@Override
	public String read(Cell cell) {
		return getCellValueAsString(cell);
	}
}
