package org.king.excooly;

import org.king.excooly.support.poi.ReadingExcelColumn;

/**
 * 值设置器
 * @author wangjw5
 */
public interface ValueSetter {

	void set(ReadingExcelColumn excelCell, Object value, Object instance);
}
