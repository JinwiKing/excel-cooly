package org.king.excool;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public interface ExcelOperator {

	<Type> List<Type> read(InputStream is, String sheetName, Class<Type> clazz);

	default <Type> void write(ExcelType excelType, String fileName, String sheetName, List<Type> data, Class<Type> dataType){
		write(excelType, fileName, sheetName, data, dataType, true);
	}

	<Type> void write(ExcelType excelType, String fileName, String sheetName, List<Type> data, Class<Type> dataType, boolean overrided);
}
