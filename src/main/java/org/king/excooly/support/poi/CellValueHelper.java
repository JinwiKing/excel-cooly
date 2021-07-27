package org.king.excooly.support.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

class CellValueHelper implements SimpleCellValueReader {
	
	static String getCellValueAsString(Cell c) {
		if (c == null) return null;
		String v = null;
		int ct = c.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_BOOLEAN: v = String.valueOf(c.getBooleanCellValue()); break;
			case Cell.CELL_TYPE_STRING: v = c.getStringCellValue(); break;
			case Cell.CELL_TYPE_FORMULA: {
				try {
					v = c.getStringCellValue();
				} catch (Exception e) {
					v = c.getCellFormula();
					byte ecc = c.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: v = String.valueOf(c.getNumericCellValue()); break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
		}
		return v;
	}

	@Override
	public String read(Cell cell) {
		return getCellValueAsString(cell);
	}
}
