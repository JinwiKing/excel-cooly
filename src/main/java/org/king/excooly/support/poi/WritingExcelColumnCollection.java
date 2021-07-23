package org.king.excooly.support.poi;

import java.util.LinkedList;
import java.util.List;

/**
 * 写入中的excel单元格
 * @author king
 */
public class WritingExcelColumnCollection {
	ExcelColumnConfigurationCollection configurationCollection;
	WritingExcelColumn idColumn;
	List<WritingExcelColumn> columns = new LinkedList<>();
	List<WritingExcelColumnCollection> cascadedCollections = new LinkedList<>();
	
	public ExcelColumnConfigurationCollection getConfigurationCollection() {
		return configurationCollection;
	}
	public WritingExcelColumn getIdColumn() {
		return idColumn;
	}
	public List<WritingExcelColumn> getColumns() {
		return columns;
	}
	public List<WritingExcelColumnCollection> getCascadedCollections() {
		return cascadedCollections;
	}
}
