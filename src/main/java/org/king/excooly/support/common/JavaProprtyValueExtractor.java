package org.king.excooly.support.common;

import java.lang.reflect.Field;

import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.poi.WritingExcelColumn;

public class JavaProprtyValueExtractor implements JavaValueGetter {
	private final Field field;

	public JavaProprtyValueExtractor(Field field) {
		this.field = field;
	}

	@Override
	public Object get(Object instance, WritingExcelColumn writingExcelCell) {
		try {
			return field.get(instance);
		} catch (Exception e) {
			throw new RuntimeException("Fail to extract value", e);
		}
	}

}
