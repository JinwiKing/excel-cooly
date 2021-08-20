package org.king.excool;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ExcelOperation {
	private static ExcelOperatorFactory factory;

	static {
		ServiceLoader<ExcelOperatorFactory> factoryServiceLoader = ServiceLoader.load(ExcelOperatorFactory.class);
		Iterator<ExcelOperatorFactory> factoryIterator = factoryServiceLoader.iterator();
		if (factoryIterator.hasNext()) factory = factoryIterator.next();
		if (factory == null) throw new RuntimeException("No ExcelOperatorFactory available");
	}

	public static <Type> List<Type> read(InputStream excelIs, String sheetName, Class<Type> clazz) {
		return factory.get().read(excelIs, sheetName, clazz);
	}

	public static <Type> void write(ExcelType excelTyp, String fileName, String sheetName, List<Type> ls, Class<Type> dataType) {
		factory.get().write(excelTyp, fileName, sheetName, ls, dataType, true);
	}
}
