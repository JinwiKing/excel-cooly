package org.king.excool;

import java.lang.reflect.Method;

public class JavaBeanMethodGetter implements JavaPropertyGetter {
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
