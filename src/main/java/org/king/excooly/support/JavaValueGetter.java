package org.king.excooly.support;

import org.king.excooly.support.poi.WritingExcelColumn;

/**
 * 值获取器接口。
 * @author wangjw5
 */
@FunctionalInterface
public interface JavaValueGetter {
	
	/**
	 * 获取属性值
	 */
	Object get(Object instance, WritingExcelColumn writingExcelCell);
}
