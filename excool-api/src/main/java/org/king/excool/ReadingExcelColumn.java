package org.king.excool;

public interface ReadingExcelColumn {
    ExcelColumnConfiguration getColumnConfiguration();

    int getColIdx();

    String getColName();

    boolean isCascaded();

    ReadingExcelColumnCollection getCascadeCollection();
}
