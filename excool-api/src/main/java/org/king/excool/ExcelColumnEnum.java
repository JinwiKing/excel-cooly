package org.king.excool;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface ExcelColumnEnum {
	
	/**
	 * java值
	 */
	String javaVal();
	
	/**
	 * excel值
	 */
	String excelVal();
}
