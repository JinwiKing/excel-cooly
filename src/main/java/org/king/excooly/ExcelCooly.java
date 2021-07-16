package org.king.excooly;

import java.io.InputStream;
import java.util.List;

public interface ExcelCooly {

	<Type> List<Type> read(InputStream is, String sheetName, Class<Type> clazz, ExcelCoolyConfiguration configuration);
}
