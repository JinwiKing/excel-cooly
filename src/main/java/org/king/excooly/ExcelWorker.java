package org.king.excooly;

import java.io.InputStream;
import java.util.List;

import org.king.excooly.support.poi.PoiExcelCoolyFactory;

public class ExcelWorker {
	private static final ExcelCoolyFactory EXCEL_COOLY_FACTORY = new PoiExcelCoolyFactory();

	public static <Type> List<Type> read(InputStream excelIs, String sheetName, Class<Type> clazz) {
		ExcelCooly cooly = EXCEL_COOLY_FACTORY.newInstance();
		return cooly.read(excelIs, sheetName, clazz);
	}

	public static void write(String fileName, String sheetName, List<BasicTestBean> ls) {
		ExcelCooly cooly = EXCEL_COOLY_FACTORY.newInstance();
		cooly.write(fileName, sheetName, ls, true);
	}

}
