package org.king.excool;

import java.util.List;

public interface ReadingExcelColumnCollection {
    ExcelColumnConfigurationCollection getConfigurationCollection();

    ReadingExcelColumn getIdColumn();

    List<ReadingExcelColumn> getColumns();

    List<ReadingExcelColumnCollection> getCascadedCollections();
}
