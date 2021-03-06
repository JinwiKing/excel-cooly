package org.king.excooly.support.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReflectionUtils {

//	public static Method getAccessibleSetterOfField(Class<?> clazz, Field field) {
//		if(clazz == null || field == null) return null;
//		
//		char[] fieldNameArr = field.getName().toCharArray();
//		String setterName = "set" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
//		Method[] methods = clazz.getDeclaredMethods();
//		for(Method method : methods) {
//			if(method.getName().equals(setterName)) {
//				// A java bean setter must have one parameter in
//				if(method.getParameterCount() == 1 && (method.getModifiers() & 1) == 1) return method;
//				return null;
//			}
//		}
//		
//		return null;
//	}

	public static Method getAccessibleSetterOfField(Class<?> clazz, Field field, Class<?> dataType) {
		if(clazz == null || field == null) return null;
		
		char[] fieldNameArr = field.getName().toCharArray();
		String setterName = "set" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
		Method setter;
		try {
			setter = clazz.getDeclaredMethod(setterName, dataType);
			if(setter != null && (setter.getModifiers() & 1) == 1) return setter;
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
				if(method.getParameterCount() == 1 && (method.getModifiers() & 1) == 1) return method;
				return null;
			}
		}
		
		return null;
	}

	public static Method getAccessibleGetterOfField(Class<?> clazz, Field field, Class<?> returnType) {
		if(clazz == null || field == null) return null;
		
		char[] fieldNameArr = field.getName().toCharArray();
		String setterName = "get" + String.valueOf(fieldNameArr[0]).toUpperCase() + new String(fieldNameArr, 1, fieldNameArr.length - 1);
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			if(method.getName().equals(setterName)) {
				// A java bean setter must have one parameter in
				if(method.getParameterCount() == 1 && (method.getModifiers() & 1) == 1 && Objects.equals(method.getReturnType(), returnType)) return method;
				return null;
			}
		}
		
		return null;
	}
}
