package org.king.excooly.support.poi;

/**
 * 写入中的excel单元格
 * @author king
 */
public class WritingExcelColumn {
	ExcelColumnConfiguration columnConfiguration;
	boolean isCascaded;
	WritingExcelColumnCollection cascadeCollection;
	
	int order;
	int width;	// Excel的单元格宽度
	String columName;
	
	WritingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
		this.order = columnConfiguration.order;
//		this.width = excelColumn.width * 256;
		this.columName = columnConfiguration.serializingName;
	}

	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}

	public boolean isCascaded() {
		return isCascaded;
	}

	public WritingExcelColumnCollection getCascadeCollection() {
		return cascadeCollection;
	}

	public int getOrder() {
		return order;
	}

	public int getWidth() {
		return width;
	}

	public String getColumName() {
		return columName;
	}
}
