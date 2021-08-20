package org.king.excool;

import java.lang.reflect.AccessibleObject;
import java.util.Map;

public class DefaultExcelColumnConfigurationBuilder {
    ExcelColumnConfigurationCollection belongCollection;

    AccessibleObject accessibleObject;

    public boolean isIdCell;
    boolean requiredForDeserializing;
    int columnIndex;
    String columnName;
    String name;
    String matchPattern;
    Class<? extends ExcelCellValueDeserializer> deserializerType;
    ExcelCellValueDeserializer deserializer;
    Class<? extends JavaPropertySetter> valueSetterType;
    JavaPropertySetter valueSetter;
    boolean requiredForSerializing;
    int order;
    String serializingName;
    Class<? extends JavaPropertyGetter> valueGetterType;
    JavaPropertyGetter valueGetter;

    boolean isContainer = false;
    boolean isArray = false;

    boolean isDateCell = false;
    // 日期类型表格参数（当注解为ExcelDateCell时生效）
    String dateFormat;

    // 枚举类型表格参数（当注解为ExcelEnumColumn时生效）
    boolean isEnum = false;
    Map<String, String> propertyExcelMap;
    String defaultExcelVal;
    Map<String, String> excelPropertyMap;
    String defaultPropertyVal;

    // 级联
    boolean isCascaded = false;
    Class<?> concreteType;
    ExcelColumnConfigurationCollection cascadeConfigurationCollection;

    public DefaultExcelColumnConfigurationBuilder belongCollection(ExcelColumnConfigurationCollection belongCollection) {
        this.belongCollection = belongCollection;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder accessibleObject(AccessibleObject accessibleObject) {
        this.accessibleObject = accessibleObject;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder idCell(boolean idCell) {
        isIdCell = idCell;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder requiredForDeserializing(boolean requiredForDeserializing) {
        this.requiredForDeserializing = requiredForDeserializing;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder columnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder columnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder matchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder deserializerType(Class<? extends ExcelCellValueDeserializer> deserializerType) {
        this.deserializerType = deserializerType;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder deserializer(ExcelCellValueDeserializer deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder valueSetterType(Class<? extends JavaPropertySetter> valueSetterType) {
        this.valueSetterType = valueSetterType;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder valueSetter(JavaPropertySetter valueSetter) {
        this.valueSetter = valueSetter;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder requiredForSerializing(boolean requiredForSerializing) {
        this.requiredForSerializing = requiredForSerializing;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder order(int order) {
        this.order = order;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder serializingName(String serializingName) {
        this.serializingName = serializingName;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder valueGetterType(Class<? extends JavaPropertyGetter> valueGetterType) {
        this.valueGetterType = valueGetterType;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder valueGetter(JavaPropertyGetter valueGetter) {
        this.valueGetter = valueGetter;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder isContainer(boolean container) {
        isContainer = container;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder isArray(boolean array) {
        isArray = array;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder isDateCell(boolean dateCell) {
        isDateCell = dateCell;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder dateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder isEnum(boolean anEnum) {
        isEnum = anEnum;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder propertyExcelMap(Map<String, String> propertyExcelMap) {
        this.propertyExcelMap = propertyExcelMap;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder defaultExcelVal(String defaultExcelVal) {
        this.defaultExcelVal = defaultExcelVal;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder excelPropertyMap(Map<String, String> excelPropertyMap) {
        this.excelPropertyMap = excelPropertyMap;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder defaultPropertyVal(String defaultPropertyVal) {
        this.defaultPropertyVal = defaultPropertyVal;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder isCascaded(boolean cascaded) {
        isCascaded = cascaded;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder concreteType(Class<?> concreteType) {
        this.concreteType = concreteType;
        return this;
    }

    public DefaultExcelColumnConfigurationBuilder cascadeConfigurationCollection(ExcelColumnConfigurationCollection cascadeConfigurationCollection) {
        this.cascadeConfigurationCollection = cascadeConfigurationCollection;
        return this;
    }

    public DefaultExcelColumnConfiguration build(){
        DefaultExcelColumnConfiguration configuration = new DefaultExcelColumnConfiguration();
        configuration.accessibleObject = accessibleObject;
        configuration.isIdCell = isIdCell;
        configuration.requiredForDeserializing = requiredForDeserializing;
        configuration.columnIndex = columnIndex;
        configuration.columnName = columnName;
        configuration.name = name;
        configuration.matchPattern = matchPattern;
        configuration.deserializerType = deserializerType;
        configuration.deserializer = deserializer;
        configuration.propertySetterType = valueSetterType;
        configuration.propertySetter = valueSetter;
        configuration.requiredForSerializing = requiredForSerializing;
        configuration.order = order;
        configuration.serializingName = serializingName;
        configuration.propertyGetterType = valueGetterType;
        configuration.propertyGetter = valueGetter;
        configuration.isContainer = isContainer;
        configuration.isArray = isArray;
        configuration.isDateCell = isDateCell;
        configuration.dateFormat = dateFormat;
        configuration.isEnum = isEnum;
        configuration.propertyExcelMap = propertyExcelMap;
        configuration.defaultExcelVal = defaultExcelVal;
        configuration.excelPropertyMap = excelPropertyMap;
        configuration.defaultPropertyVal = defaultPropertyVal;
        configuration.isCascaded = isCascaded;
        configuration.concreteType = concreteType;
        configuration.cascadeConfigurationCollection = cascadeConfigurationCollection;
        return configuration;
    }
}
