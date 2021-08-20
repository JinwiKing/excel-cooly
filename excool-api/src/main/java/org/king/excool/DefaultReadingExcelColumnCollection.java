package org.king.excool;

import java.util.LinkedList;
import java.util.List;

public class DefaultReadingExcelColumnCollection implements ReadingExcelColumnCollection {
	ExcelColumnConfigurationCollection configurationCollection;
	ReadingExcelColumn idColumn;
	List<ReadingExcelColumn> columns = new LinkedList<>();
	List<ReadingExcelColumnCollection> cascadedCollections = new LinkedList<>();

	@Override
	public ExcelColumnConfigurationCollection getConfigurationCollection() {
		return configurationCollection;
	}

	public void setConfigurationCollection(ExcelColumnConfigurationCollection configurationCollection) {
		this.configurationCollection = configurationCollection;
	}

	@Override
	public ReadingExcelColumn getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(ReadingExcelColumn idColumn) {
		this.idColumn = idColumn;
	}

	@Override
	public List<ReadingExcelColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ReadingExcelColumn> columns) {
		this.columns = columns;
	}

	@Override
	public List<ReadingExcelColumnCollection> getCascadedCollections() {
		return cascadedCollections;
	}

	public void setCascadedCollections(List<ReadingExcelColumnCollection> cascadedCollections) {
		this.cascadedCollections = cascadedCollections;
	}
}
