package org.king.excooly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 级联标记
 * @author king
 */
@Inherited
@Target({
	ElementType.FIELD,
	ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCascadeCell {

	/**
	 * 如果java类型是容器类型，需要提供具体类型
	 */
	Class<?> concreteType() default NoConcreteType.class;
}
