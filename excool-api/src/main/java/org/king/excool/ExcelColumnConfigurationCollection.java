package org.king.excool;

import java.util.List;

public interface ExcelColumnConfigurationCollection {
    Class<?> getType();

    DefaultExcelColumnConfiguration getIdColumnConfiguration();

    List<DefaultExcelColumnConfiguration> getConfigurations();

    List<ExcelColumnConfigurationCollection> getCascadedConfigurationCollections();
}
