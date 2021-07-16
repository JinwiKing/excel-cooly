package org.king.excooly;

import org.king.excooly.support.poi.ExcelRowValueWritingParam;

/**
 * Excel行数据写入器
 * @author wangjw5
 */
@FunctionalInterface
public interface ExcelRowValueWriter {
	
	/**
	 * 将数据写入到指定行
	 * @param writingParam 写参数
	 * @return 下次写入的行编号。一般为 writingParam.writeToLineNo + 1，返回的编号将用于下次传入
	 */
	int write(ExcelRowValueWritingParam writingParam);
}
