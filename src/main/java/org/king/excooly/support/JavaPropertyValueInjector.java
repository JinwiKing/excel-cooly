package org.king.excooly.support;

import java.lang.reflect.Field;

import org.king.excooly.support.poi.ReadingExcelColumn;

public class JavaPropertyValueInjector implements JavaValueSetter {
	
	private final Field field;

	public JavaPropertyValueInjector(Field field) {
		this.field = field;
	}

	@Override
	public void set(Object instance, Object value, ReadingExcelColumn readingColumn){
		try {
			field.set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException("Fail to inject value", e);
		}
	}

}
