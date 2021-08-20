package org.king.excool;

import java.util.List;

public interface WritingExcelColumnCollection {
    ExcelColumnConfigurationCollection getConfigurationCollection();

    WritingExcelColumn getIdColumn();

    List<WritingExcelColumn> getColumns();

    List<WritingExcelColumnCollection> getCascadedCollections();
}
