package org.king.excooly;

import java.io.InputStream;
import java.util.List;

public interface ExcelOperation {

	<Type> List<Type> read(InputStream is, String sheetName, Class<Type> clazz);

	<Type> void write(ExcelType excelType, String fileName, String sheetName, List<Type> data, Class<Type> dataType, boolean overrided);
}
