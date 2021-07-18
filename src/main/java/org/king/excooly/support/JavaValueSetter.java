package org.king.excooly.support;

import org.king.excooly.support.poi.FocusingExcelColumn;

/**
 * java值注入器
 * @author king
 */
public interface JavaValueSetter {

	void set(Object instance, Object value, FocusingExcelColumn readingColumn);
}
