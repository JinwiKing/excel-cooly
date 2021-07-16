package org.king.excooly;

import java.io.InputStream;
import java.util.List;

public class ExcelWorker {

	public static <Type> List<Type> read(InputStream excelIs, String sheetName, Class<Type> clazz) {
		ExcelCooly cooly = ExcelCoolyFactory.newInstance();
		return cooly.read(excelIs, sheetName, clazz);
	}

}
