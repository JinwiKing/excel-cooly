package org.king.excool;

import java.lang.reflect.AccessibleObject;
import java.util.Map;

public interface ExcelColumnConfiguration {
    int getOrder();

    String getName();

    String getDateFormat();

    ExcelCellValueDeserializer getDeserializer();

    JavaPropertySetter getPropertySetter();

    AccessibleObject getAccessibleObject();

    boolean isRequiredForDeserializing();

    int getColumnIndex();

    String getColumnName();

    String getMatchPattern();

    Class<? extends ExcelCellValueDeserializer> getDeserializerType();

    Class<? extends JavaPropertySetter> getPropertySetterType();

    boolean isRequiredForSerializing();

    Class<? extends JavaPropertyGetter> getPropertyGetterType();

    JavaPropertyGetter getPropertyGetter();

    boolean isEnum();

    Map<String, String> getPropertyExcelMap();

    String getDefaultExcelVal();

    Map<String, String> getExcelPropertyMap();

    String getDefaultPropertyVal();

    ExcelColumnConfigurationCollection getBelongCollection();

    boolean isIdCell();

    String getSerializingName();

    boolean isContainer();

    boolean isArray();

    boolean isDateCell();

    boolean isCascaded();

    Class<?> getConcreteType();

    ExcelColumnConfigurationCollection getCascadeConfigurationCollection();
}
