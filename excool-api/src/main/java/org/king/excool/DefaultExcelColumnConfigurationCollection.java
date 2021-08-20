package org.king.excool;

import java.util.LinkedList;
import java.util.List;

public class DefaultExcelColumnConfigurationCollection implements ExcelColumnConfigurationCollection {
	Class<?> type;
	DefaultExcelColumnConfiguration idColumnConfiguration;
	List<DefaultExcelColumnConfiguration> configurations = new LinkedList<>();
	List<ExcelColumnConfigurationCollection> cascadedCollections = new LinkedList<>();
	
	@Override
	public Class<?> getType() {
		return type;
	}
	@Override
	public DefaultExcelColumnConfiguration getIdColumnConfiguration() {
		return idColumnConfiguration;
	}
	@Override
	public List<DefaultExcelColumnConfiguration> getConfigurations() {
		return configurations;
	}
	@Override
	public List<ExcelColumnConfigurationCollection> getCascadedConfigurationCollections() {
		return cascadedCollections;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public void setIdColumnConfiguration(DefaultExcelColumnConfiguration idColumnConfiguration) {
		this.idColumnConfiguration = idColumnConfiguration;
	}

	public void setConfigurations(List<DefaultExcelColumnConfiguration> configurations) {
		this.configurations = configurations;
	}

	public void setCascadedCollections(List<ExcelColumnConfigurationCollection> cascadedCollections) {
		this.cascadedCollections = cascadedCollections;
	}
}
