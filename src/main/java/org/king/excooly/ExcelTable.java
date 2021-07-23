package org.king.excooly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel表格设置注解
 * @author wangjw5
 */
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {
	
	/**
	 * 表头高度
	 */
	short titleHeight() default (short) (52 * 20);
	
	/**
	 * 表头是否包裹文字
	 */
	boolean titleWrapText() default true;
	
	/**
	 * 数据行单元格高度
	 */
	short dataRowHeight() default 19 * 20;
	
	/**
	 * 反序列化时默认读取的表头行
	 */
	int titleAt() default 0;
}
