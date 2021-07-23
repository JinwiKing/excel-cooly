package org.king.excooly;

import java.io.InputStream;
import java.util.List;

import org.king.excooly.support.poi.PoiExcelCoolyFactory;

public class ExcelOperator {
	private static final ExcelCoolyFactory EXCEL_COOLY_FACTORY = new PoiExcelCoolyFactory();

	public static <Type> List<Type> read(InputStream excelIs, String sheetName, Class<Type> clazz) {
		ExcelOperation cooly = EXCEL_COOLY_FACTORY.newInstance();
		return cooly.read(excelIs, sheetName, clazz);
	}

	public static <Type> void write(ExcelType excelTyp, String fileName, String sheetName, List<Type> ls, Class<Type> dataType) {
		ExcelOperation cooly = EXCEL_COOLY_FACTORY.newInstance();
		cooly.write(excelTyp, fileName, sheetName, ls, dataType, true);
	}
}
