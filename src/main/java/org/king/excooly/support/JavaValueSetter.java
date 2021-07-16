package org.king.excooly.support;

import org.king.excooly.support.poi.ReadingExcelColumn;

/**
 * java值注入器
 * @author king
 */
public interface JavaValueSetter {

	void set(Object instance, Object value, ReadingExcelColumn readingColumn);
}
