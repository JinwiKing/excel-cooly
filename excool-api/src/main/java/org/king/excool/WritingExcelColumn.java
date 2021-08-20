package org.king.excool;

public interface WritingExcelColumn {
    ExcelColumnConfiguration getColumnConfiguration();

    boolean isCascaded();

    WritingExcelColumnCollection getCascadeCollection();

    int getOrder();

    int getWidth();

    String getColumName();
}
