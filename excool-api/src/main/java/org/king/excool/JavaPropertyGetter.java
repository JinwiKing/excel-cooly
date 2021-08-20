package org.king.excool;

/**
 * 值获取器接口。
 * @author wangjw5
 */
@FunctionalInterface
public interface JavaPropertyGetter {
	
	/**
	 * 获取属性值
	 */
	Object get(Object instance, WritingExcelColumn writingExcelCell);
}
