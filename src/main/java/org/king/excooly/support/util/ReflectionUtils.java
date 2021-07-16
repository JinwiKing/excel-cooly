package org.king.excooly.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

	public static Method getAccessibleSetterOfField(Class<?> clazz, Field field) {
		if(clazz == null || field == null) return null;
		
		char[] fieldNameArr = field.getName().toCharArray();
		String setterName = "set" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getName().equals(setterName)) {
				// A java bean setter must have one parameter in
				if(method.getParameterCount() == 1 && method.isAccessible()) return method;
				return null;
			}
		}
		
		return null;
	}

	public static Method getAccessibleSetterOfField(Class<?> clazz, Field field, Class<?> dataType) {
		if(clazz == null || field == null) return null;
		
		char[] fieldNameArr = field.getName().toCharArray();
		String setterName = "set" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
		Method setter;
		try {
			setter = clazz.getDeclaredMethod(setterName, dataType);
			if(setter != null && setter.isAccessible()) return setter;
		} catch (Exception e) {
			// don't care
		}
		
		return null;
	}

	public static Method getAccessibleGetterOfField(Class<?> clazz, Field field) {
		if(clazz == null || field == null) return null;
		
		char[] fieldNameArr = field.getName().toCharArray();
		String setterName = "get" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getName().equals(setterName)) {
				// A java bean setter must have one parameter in
				if(method.getParameterCount() == 1 && method.isAccessible()) return method;
				return null;
			}
		}
		
		return null;
	}
}
