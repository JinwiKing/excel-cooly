package org.king.excooly.support.common;

import java.lang.reflect.Field;

import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.poi.ReadingExcelColumn;

public class JavaPropertyValueInjector implements JavaValueSetter {
	
	private final Field field;
	private final boolean isBaseType;

	public JavaPropertyValueInjector(Field field) {
		this.field = field;
		Class<?> type = field.getType();
		this.isBaseType = type.equals(byte.class) || type.equals(short.class) || type.equals(int.class) ||
				type.equals(long.class) || type.equals(float.class) || type.equals(double.class) ||
				type.equals(char.class) || type.equals(boolean.class);
	}

	@Override
	public void set(Object instance, Object value, ReadingExcelColumn readingColumn){
		try {
			if(value == null && isBaseType) return;
			field.set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException("Fail to inject value", e);
		}
	}

}
