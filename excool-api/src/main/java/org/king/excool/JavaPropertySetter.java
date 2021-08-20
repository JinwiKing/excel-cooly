package org.king.excool;

/**
 * java值注入器
 * @author king
 */
public interface JavaPropertySetter {

	void set(Object instance, Object value, ReadingExcelColumn readingColumn);
}
