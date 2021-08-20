package org.king.excool;

import java.lang.reflect.Field;

public class JavaPropertyPropertyInjector implements JavaPropertySetter {
	
	private final Field field;
	private final boolean isBaseType;

	public JavaPropertyPropertyInjector(Field field) {
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
