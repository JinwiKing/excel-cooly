package org.king.excooly.support.poi;

import java.util.LinkedList;
import java.util.List;

public class ExcelColumnConfigurationCollection {
	Class<?> type;
	ExcelColumnConfiguration idColumnConfiguration;
	List<ExcelColumnConfiguration> configurations = new LinkedList<>();
	List<ExcelColumnConfigurationCollection> cascadedCollections = new LinkedList<>();
	
	public Class<?> getType() {
		return type;
	}
	public ExcelColumnConfiguration getIdColumnConfiguration() {
		return idColumnConfiguration;
	}
	public List<ExcelColumnConfiguration> getConfigurations() {
		return configurations;
	}
	public List<ExcelColumnConfigurationCollection> getCascadedConfigurationCollections() {
		return cascadedCollections;
	}
}
