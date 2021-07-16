package org.king.excooly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Excel表格设置注解
 * @author wangjw5
 */
@Target({
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {
	
	Class<? extends ExcelRowValueWriter> rowValueWriter() default DefaultExcelRowValueWriter.class;
	
	/**
	 * 表头高度
	 */
	short titleHeight() default (short) (52 * 20);
	
	/**
	 * 表头单元格水平对齐方式
	 */
	short titleAlignment() default CellStyle.ALIGN_CENTER;
	
	/**
	 * 表头单元格垂直对齐方式
	 */
	short titleVerticalAlignment() default CellStyle.VERTICAL_CENTER;
	
	/**
	 * 表头单元格上边框样式
	 */
	short titleBorderTop() default CellStyle.BORDER_THIN;
	
	/**
	 * 表头单元格下边框样式
	 */
	short titleBorderBottom() default CellStyle.BORDER_THIN;
	
	/**
	 * 表头单元格左边框样式
	 */
	short titleBorderLeft() default CellStyle.BORDER_THIN;
	
	/**
	 * 表头单元格右边框样式
	 */
	short titleBorderRight() default CellStyle.BORDER_THIN;
	
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
