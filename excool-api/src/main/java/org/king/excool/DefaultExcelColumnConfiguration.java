package org.king.excool;

import java.lang.reflect.AccessibleObject;
import java.util.Map;

/**
 * 解析ExcelColumn注解得到的配置信息
 * @author king
 */
public class DefaultExcelColumnConfiguration implements ExcelColumnConfiguration {
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
	Class<? extends JavaPropertySetter> propertySetterType;
	JavaPropertySetter propertySetter;
	boolean requiredForSerializing;
	int order;
	String serializingName;
	Class<? extends JavaPropertyGetter> propertyGetterType;
	JavaPropertyGetter propertyGetter;
	
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

	@Override
	public ExcelColumnConfigurationCollection getBelongCollection() {
		return belongCollection;
	}

	public void setBelongCollection(ExcelColumnConfigurationCollection belongCollection) {
		this.belongCollection = belongCollection;
	}

	@Override
	public AccessibleObject getAccessibleObject() {
		return accessibleObject;
	}

	public void setAccessibleObject(AccessibleObject accessibleObject) {
		this.accessibleObject = accessibleObject;
	}

	@Override
	public boolean isIdCell() {
		return isIdCell;
	}

	public void setIdCell(boolean idCell) {
		isIdCell = idCell;
	}

	@Override
	public boolean isRequiredForDeserializing() {
		return requiredForDeserializing;
	}

	public void setRequiredForDeserializing(boolean requiredForDeserializing) {
		this.requiredForDeserializing = requiredForDeserializing;
	}

	@Override
	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getMatchPattern() {
		return matchPattern;
	}

	public void setMatchPattern(String matchPattern) {
		this.matchPattern = matchPattern;
	}

	@Override
	public Class<? extends ExcelCellValueDeserializer> getDeserializerType() {
		return deserializerType;
	}

	public void setDeserializerType(Class<? extends ExcelCellValueDeserializer> deserializerType) {
		this.deserializerType = deserializerType;
	}

	@Override
	public ExcelCellValueDeserializer getDeserializer() {
		return deserializer;
	}

	public void setDeserializer(ExcelCellValueDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	@Override
	public Class<? extends JavaPropertySetter> getPropertySetterType() {
		return propertySetterType;
	}

	public void setPropertySetterType(Class<? extends JavaPropertySetter> propertySetterType) {
		this.propertySetterType = propertySetterType;
	}

	@Override
	public JavaPropertySetter getPropertySetter() {
		return propertySetter;
	}

	public void setPropertySetter(JavaPropertySetter propertySetter) {
		this.propertySetter = propertySetter;
	}

	@Override
	public boolean isRequiredForSerializing() {
		return requiredForSerializing;
	}

	public void setRequiredForSerializing(boolean requiredForSerializing) {
		this.requiredForSerializing = requiredForSerializing;
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public String getSerializingName() {
		return serializingName;
	}

	public void setSerializingName(String serializingName) {
		this.serializingName = serializingName;
	}

	@Override
	public Class<? extends JavaPropertyGetter> getPropertyGetterType() {
		return propertyGetterType;
	}

	public void setPropertyGetterType(Class<? extends JavaPropertyGetter> propertyGetterType) {
		this.propertyGetterType = propertyGetterType;
	}

	@Override
	public JavaPropertyGetter getPropertyGetter() {
		return propertyGetter;
	}

	public void setPropertyGetter(JavaPropertyGetter propertyGetter) {
		this.propertyGetter = propertyGetter;
	}

	@Override
	public boolean isContainer() {
		return isContainer;
	}

	public void setContainer(boolean container) {
		isContainer = container;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean array) {
		isArray = array;
	}

	@Override
	public boolean isDateCell() {
		return isDateCell;
	}

	public void setDateCell(boolean dateCell) {
		isDateCell = dateCell;
	}

	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public boolean isEnum() {
		return isEnum;
	}

	public void setEnum(boolean anEnum) {
		isEnum = anEnum;
	}

	@Override
	public Map<String, String> getPropertyExcelMap() {
		return propertyExcelMap;
	}

	public void setPropertyExcelMap(Map<String, String> propertyExcelMap) {
		this.propertyExcelMap = propertyExcelMap;
	}

	@Override
	public String getDefaultExcelVal() {
		return defaultExcelVal;
	}

	public void setDefaultExcelVal(String defaultExcelVal) {
		this.defaultExcelVal = defaultExcelVal;
	}

	@Override
	public Map<String, String> getExcelPropertyMap() {
		return excelPropertyMap;
	}

	public void setExcelPropertyMap(Map<String, String> excelPropertyMap) {
		this.excelPropertyMap = excelPropertyMap;
	}

	@Override
	public String getDefaultPropertyVal() {
		return defaultPropertyVal;
	}

	public void setDefaultPropertyVal(String defaultPropertyVal) {
		this.defaultPropertyVal = defaultPropertyVal;
	}

	@Override
	public boolean isCascaded() {
		return isCascaded;
	}

	public void setCascaded(boolean cascaded) {
		isCascaded = cascaded;
	}

	@Override
	public Class<?> getConcreteType() {
		return concreteType;
	}

	public void setConcreteType(Class<?> concreteType) {
		this.concreteType = concreteType;
	}

	@Override
	public ExcelColumnConfigurationCollection getCascadeConfigurationCollection() {
		return cascadeConfigurationCollection;
	}

	public void setCascadeConfigurationCollection(ExcelColumnConfigurationCollection cascadeConfigurationCollection) {
		this.cascadeConfigurationCollection = cascadeConfigurationCollection;
	}
}
