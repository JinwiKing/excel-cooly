package org.king.excool;

import java.lang.reflect.Method;

public class JavaBeanMethorSetter implements JavaPropertySetter {
	private final Method setter;

	public JavaBeanMethorSetter(Method setter) {
		this.setter = setter;
	}

	@Override
	public void set(Object instance, Object value, ReadingExcelColumn readingColumn){
		try {
			setter.invoke(instance, value);
		} catch (Exception e) {
			throw new RuntimeException("Fail to set value " + value + " into instance " + instance + " by method " + setter, e);
		}
	}
}
