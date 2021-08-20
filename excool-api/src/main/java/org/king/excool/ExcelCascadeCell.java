package org.king.excool;

import java.lang.annotation.*;

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
