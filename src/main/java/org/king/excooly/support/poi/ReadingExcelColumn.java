package org.king.excooly.support.poi;

/**
 * 读取种的excel单元格
 * @author wangjw5
 */
public class ReadingExcelColumn extends FocusingExcelColumn {
	int colIdx;
	String colName;
	
	public ReadingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		super(columnConfiguration);
	}

	public int getColIdx() {
		return colIdx;
	}

	public String getColName() {
		return colName;
	}
}
