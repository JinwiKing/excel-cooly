package org.king.excool;

import java.util.LinkedList;
import java.util.List;

/**
 * 写入中的excel单元格
 * @author king
 */
public class DefaultWritingExcelColumnCollection implements WritingExcelColumnCollection {
	ExcelColumnConfigurationCollection configurationCollection;
	WritingExcelColumn idColumn;
	List<WritingExcelColumn> columns = new LinkedList<>();
	List<WritingExcelColumnCollection> cascadedCollections = new LinkedList<>();

	@Override
	public ExcelColumnConfigurationCollection getConfigurationCollection() {
		return configurationCollection;
	}

	public void setConfigurationCollection(ExcelColumnConfigurationCollection configurationCollection) {
		this.configurationCollection = configurationCollection;
	}

	@Override
	public WritingExcelColumn getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(WritingExcelColumn idColumn) {
		this.idColumn = idColumn;
	}

	@Override
	public List<WritingExcelColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<WritingExcelColumn> columns) {
		this.columns = columns;
	}

	@Override
	public List<WritingExcelColumnCollection> getCascadedCollections() {
		return cascadedCollections;
	}

	public void setCascadedCollections(List<WritingExcelColumnCollection> cascadedCollections) {
		this.cascadedCollections = cascadedCollections;
	}
}
