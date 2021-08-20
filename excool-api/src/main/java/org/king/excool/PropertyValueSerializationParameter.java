package org.king.excool;

public interface PropertyValueSerializationParameter {
    Object getJavaValue();

    ExcelColumnConfiguration getColumnConfiguration();
}
