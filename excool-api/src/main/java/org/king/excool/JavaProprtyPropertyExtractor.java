package org.king.excool;

import java.lang.reflect.Field;

public class JavaProprtyPropertyExtractor implements JavaPropertyGetter {
	private final Field field;

	public JavaProprtyPropertyExtractor(Field field) {
		this.field = field;
	}

	@Override
	public Object get(Object instance, WritingExcelColumn writingExcelCell) {
		try {
			return field.get(instance);
		} catch (Exception e) {
			throw new RuntimeException("Fail to extract value", e);
		}
	}

}
