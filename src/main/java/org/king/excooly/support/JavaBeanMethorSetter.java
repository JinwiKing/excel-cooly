package org.king.excooly.support;

import java.lang.reflect.Method;

import org.king.excooly.support.poi.ReadingExcelColumn;

public class JavaBeanMethorSetter implements JavaValueSetter {
	private final Method setter;

	public JavaBeanMethorSetter(Method setter) {
		this.setter = setter;
	}

	@Override
	public void set(Object instance, Object value, ReadingExcelColumn readingColumn){
		try {
			setter.invoke(instance, value);
		} catch (Exception e) {
			throw new RuntimeException("Fail to set value", e);
		}
	}
}
