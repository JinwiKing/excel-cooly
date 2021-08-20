package org.king.excool;

/**
 * 写入中的excel单元格
 * @author king
 */
public class DefaultWritingExcelColumn implements WritingExcelColumn {
	ExcelColumnConfiguration columnConfiguration;
	boolean isCascaded;
	WritingExcelColumnCollection cascadeCollection;
	
	int order;
	int width;	// Excel的单元格宽度
	String columName;
	
	public DefaultWritingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
		this.order = columnConfiguration.getOrder();
//		this.width = excelColumn.width * 256;
		this.columName = columnConfiguration.getSerializingName();
	}

	@Override
	public ExcelColumnConfiguration getColumnConfiguration() {
		return columnConfiguration;
	}

	public void setColumnConfiguration(ExcelColumnConfiguration columnConfiguration) {
		this.columnConfiguration = columnConfiguration;
	}

	@Override
	public boolean isCascaded() {
		return isCascaded;
	}

	public void setCascaded(boolean cascaded) {
		isCascaded = cascaded;
	}

	@Override
	public WritingExcelColumnCollection getCascadeCollection() {
		return cascadeCollection;
	}

	public void setCascadeCollection(WritingExcelColumnCollection cascadeCollection) {
		this.cascadeCollection = cascadeCollection;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String getColumName() {
		return columName;
	}

	public void setColumName(String columName) {
		this.columName = columName;
	}
}
