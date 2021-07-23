package org.king.excooly.support.poi;

import java.util.LinkedList;
import java.util.List;

public class ReadingExcelColumnCollection {
	ExcelColumnConfigurationCollection configurationCollection;
	ReadingExcelColumn idColumn;
	List<ReadingExcelColumn> columns = new LinkedList<>();
	List<ReadingExcelColumnCollection> cascadedCollections = new LinkedList<>();

	public ExcelColumnConfigurationCollection getConfigurationCollection() {
		return configurationCollection;
	}

	public ReadingExcelColumn getIdColumn() {
		return idColumn;
	}

	public List<ReadingExcelColumn> getColumns() {
		return columns;
	}

	public List<ReadingExcelColumnCollection> getCascadedCollections() {
		return cascadedCollections;
	}
}
