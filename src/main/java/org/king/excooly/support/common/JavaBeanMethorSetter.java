package org.king.excooly.support.common;

import java.lang.reflect.Method;

import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.poi.FocusingExcelColumn;

public class JavaBeanMethorSetter implements JavaValueSetter {
	private final Method setter;

	public JavaBeanMethorSetter(Method setter) {
		this.setter = setter;
	}

	@Override
	public void set(Object instance, Object value, FocusingExcelColumn readingColumn){
		try {
			setter.invoke(instance, value);
		} catch (Exception e) {
			throw new RuntimeException("Fail to set value " + value + " into instance " + instance + " by method " + setter, e);
		}
	}
}
