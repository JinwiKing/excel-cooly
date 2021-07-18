package org.king.excooly.support.common;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.ExcelTable;
import org.king.excooly.ExcelType;
import org.king.excooly.ExcelWriter;

public class ExcelUtils {
	
	public static ExcelType detectExcelTypeFromStream(InputStream is) {
		try {
			byte[] bytes = new byte[4];
			is.mark(4);
			is.read(bytes);
			is.reset();
			if((bytes[0] & 0x000000D0) == 0x000000D0 && (bytes[1] & 0x000000CF) == 0x000000CF && 
				(bytes[2] & 0x00000011) == 0x00000011 && (bytes[3] & 0x000000E0) == 0x000000E0) return ExcelType.XLS;
			else if((bytes[0] & 0x00000050) == 0x00000050 && (bytes[1] & 0x0000004B) == 0x0000004B && 
				(bytes[2] & 0x00000003) == 0x00000003 && (bytes[3] & 0x00000004) == 0x00000004) return ExcelType.XLSX;
			else return ExcelType.UNKNOWN;
		} catch (Exception e) {
			throw new RuntimeException("Can detect excel type", e);
		}
	}
	
	public static String getCellValueAsString(Cell c) {
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
	
	public static Sheet getOrCreateSheet(Workbook wb, String sheetName) {
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null) sheet = wb.createSheet(sheetName);
		return sheet;
	}
	
	public static Row getOrCreateRow(Sheet sheet, int rowNo) {
		Row row = sheet.getRow(rowNo);
		if(row == null) row = sheet.createRow(rowNo);
		return row;
	}
	
	public static Cell getOrCreateCell(Row row, int colNo) {
		Cell cell = row.getCell(colNo);
		if(cell == null) cell = row.createCell(colNo);
		return cell;
	}
	
	static ExcelTable getOrUseDefaultExcelTableAnnotation(Class<?> dataType) {
		ExcelTable excelTable = dataType.getAnnotation(ExcelTable.class);
		if(excelTable == null) excelTable = ExcelWriter.class.getDeclaredAnnotation(ExcelTable.class);
		return excelTable;
	}
}
