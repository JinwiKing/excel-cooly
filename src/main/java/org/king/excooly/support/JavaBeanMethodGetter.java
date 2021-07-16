package org.king.excooly.support;

import java.lang.reflect.Method;

import org.king.excooly.support.poi.WritingExcelColumn;

public class JavaBeanMethodGetter implements JavaValueGetter {
	private final Method method;
	
	public JavaBeanMethodGetter(Method method) {
		this.method = method;
	}

	@Override
	public Object get(Object instance, WritingExcelColumn writingExcelCell) {
		try {
			return method.invoke(instance);
		} catch (Exception e) {
			throw new RuntimeException("Fail to get value", e);
		}
	}

}
