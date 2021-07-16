package org.king.excooly;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.BigDecimalCodec;
import org.king.excooly.support.poi.ByteCodec;
import org.king.excooly.support.poi.DateCodec;
import org.king.excooly.support.poi.DoubleCodec;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.FloatCodec;
import org.king.excooly.support.poi.IntegerCodec;
import org.king.excooly.support.poi.LongCodec;
import org.king.excooly.support.poi.ReadingExcelColumn;
import org.king.excooly.support.poi.ShortCodec;
import org.king.excooly.support.poi.StringCodec;
import org.king.excooly.support.poi.UsingDefaultValueDeserializer;
import org.king.excooly.support.poi.UsingDefaultValueSetter;

import com.centaline.util.ReflectionUtils;

/**
 * Excel表格读取器。
 * @version 1.0
 * @author wangjw5
 * */
public class ExcelReader {
	
	/**
	 * 属性值JavaBeanSetter注入器
	 */
	private static final ValueSetter PROPERTY_VALUE_JAVA_BEAN_SETTER = new ValueSetter() {

		@Override
		public void set(ReadingExcelColumn excelCell, Object value, Object instance) {
			try {
				excelCell.objctJavaBeanSetter.invoke(instance, value);
			} catch (Exception e) {
				throw new RuntimeException("注入值到的实例失败", e);
			}
		}
	};

	/**
	 * 属性值直接注入器
	 */
	private static final ValueSetter PROPERTY_VALUE_REFLECTION_SETTER = new ValueSetter() {

		@Override
		public void set(ReadingExcelColumn excelCell, Object value, Object instance) {
			Field field = (Field) excelCell.annotatedObject;
			if(!field.isAccessible()) field.setAccessible(true);
			try {
				field.set(instance, value);
			} catch (Exception e) {
				throw new RuntimeException("注入值到的实例失败", e);
			}
		}
	};

	/**
	 * 属性值方法注入器
	 */
	private static final ValueSetter PROPERTY_VALUE_METHOD_SETTER = new ValueSetter() {

		@Override
		public void set(ReadingExcelColumn excelCell, Object value, Object instance) {
			try {
				excelCell.objctJavaBeanSetter.invoke(instance, value);
			} catch (Exception e) {
				throw new RuntimeException("注入值到实例失败", e);
			}
		}
	};
	
	private static final Logger LOGGER = Logger.getLogger(ExcelReader.class);
	private static final Map<ExcelColumnMatcher, ReadingExcelColumn> MATCHER_TO_EXCEL_COLUMN = new WeakHashMap<>();
	private static final Map<Class<?>, ExcelCellValueDeserializer> DEFAULT_DESERIALIZERS;
	
	static {
		Map<Class<?>, ExcelCellValueDeserializer> defaultDeserializers = new HashMap<>();
		defaultDeserializers.put(byte.class, new ByteCodec());
		defaultDeserializers.put(Byte.class, new ByteCodec());
		defaultDeserializers.put(short.class, new ShortCodec());
		defaultDeserializers.put(Short.class, new ShortCodec());
		defaultDeserializers.put(int.class, new IntegerCodec());
		defaultDeserializers.put(Integer.class, new IntegerCodec());
		defaultDeserializers.put(long.class, new LongCodec());
		defaultDeserializers.put(Long.class, new LongCodec());
		defaultDeserializers.put(float.class, new FloatCodec());
		defaultDeserializers.put(Float.class, new FloatCodec());
		defaultDeserializers.put(double.class, new DoubleCodec());
		defaultDeserializers.put(Double.class, new DoubleCodec());
		defaultDeserializers.put(String.class, new StringCodec());
		defaultDeserializers.put(Date.class, new DateCodec());
		defaultDeserializers.put(BigDecimal.class, new BigDecimalCodec());
		DEFAULT_DESERIALIZERS = Collections.unmodifiableMap(defaultDeserializers);
	}
	
	public static <DataType> List<DataType> readFrom(Workbook workbook, String sheetName, Class<DataType> dataType) {
		if (workbook == null) throw new IllegalArgumentException("Work book is null");
		else if (sheetName == null) throw new IllegalArgumentException("Sheet name is null");
		else if (dataType == null) throw new IllegalArgumentException("Data type is null");
		
		Sheet sheet = workbook.getSheet(sheetName);
		if(sheet == null) return new ArrayList<>(0);
		return readFrom(workbook, sheet, dataType);
	}
	
	/**
	 * 重表格中读取数据
	 */
	public static <DataType> List<DataType> readFrom(Workbook workbook, Sheet sheet, Class<DataType> dataType){
		if (workbook == null) throw new IllegalArgumentException("Work book is null");
		else if (sheet == null) throw new IllegalArgumentException("Sheet is null");
		else if (dataType == null) throw new IllegalArgumentException("Data type is null");
		
		Class<?> lookingClass = dataType;
		Set<ReadingExcelColumn> containedCols = new HashSet<>(), requiredCols = new HashSet<>();
		Set<ExcelColumnMatcher> matchers = new HashSet<>();
		while(lookingClass != Object.class) {
			
			// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
			// property or a method of an instance, so we need to scan all properties 
			// and all method of the instance.
			
			// On field
			for(Field field : lookingClass.getDeclaredFields()) {
				ExcelColumnConfiguration colCfg = ExcelUtils.getColumnConfiguration(field);
				if(colCfg != null && !colCfg.ignoreDeserialization) {
					
					// 获取反序列化器
					Class<?> limitedType = null;
					ExcelCellValueDeserializer deserializer = null;
					boolean isUsingDefaultDeserializer = false;
					if(!colCfg.deserializerType.equals(UsingDefaultValueDeserializer.class)) {
						try {
							deserializer = colCfg.deserializerType.newInstance();
						} catch (Exception e) {
							throw new RuntimeException("无法实例化属性设置器", e);
						}
					}else {
						limitedType = field.getType();
						deserializer = DEFAULT_DESERIALIZERS.get(limitedType);
						isUsingDefaultDeserializer = true;
					}
					
					Method javaBeanSetter = null;
					ValueSetter valueSetter = null;
					if(!colCfg.valueSetterType.equals(UsingDefaultValueSetter.class)) {
						try {
							valueSetter = colCfg.valueSetterType.newInstance();
						} catch (Exception e) {
							throw new RuntimeException("无法实例化属性注入器", e);
						}
					}else {
						if(limitedType != null) {
							try {
								javaBeanSetter = ReflectionUtils.getSetterOfFied(field, lookingClass, limitedType);
							} catch (Exception e) {
								if(LOGGER.isDebugEnabled()) LOGGER.debug("无法获取JavaBean的Setter方法，使用反射设置属性值的方法设置属性 " + field.getName());
							}
						}else javaBeanSetter = ReflectionUtils.getSetterOfFied(field, lookingClass);
						valueSetter = javaBeanSetter != null ? PROPERTY_VALUE_JAVA_BEAN_SETTER : PROPERTY_VALUE_REFLECTION_SETTER;
					}
					
					ReadingExcelColumn readingExcelColumn = new ReadingExcelColumn(colCfg, field, javaBeanSetter, -1, null, deserializer, valueSetter);
					readingExcelColumn.isUsingDefaultDeserializer = isUsingDefaultDeserializer;
					
					containedCols.add(readingExcelColumn);
					if(colCfg.required) requiredCols.add(readingExcelColumn);
					
					ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(readingExcelColumn);
					matchers.add(usingMatcher);
					MATCHER_TO_EXCEL_COLUMN.put(usingMatcher, readingExcelColumn);
				}
			}
			
			// On method
			for(Method method : lookingClass.getDeclaredMethods()) {
				ExcelColumnConfiguration colCfg = ExcelUtils.getColumnConfiguration(method);
				if(colCfg != null && !colCfg.ignoreDeserialization) {
					int numParams = method.getParameterCount();
					if(numParams == 0) continue;	// May be a getter
					else if(numParams != 1) throw new RuntimeException("请确保使用用于设置Excel值的方法只接受一个参数");
					
					Class<?> limitedType = method.getParameterTypes()[0];
					ExcelCellValueDeserializer deserializer = DEFAULT_DESERIALIZERS.get(limitedType);

					ReadingExcelColumn readingExcelColumn = new ReadingExcelColumn(colCfg, method, method, -1, null, deserializer, PROPERTY_VALUE_METHOD_SETTER);
					readingExcelColumn.isUsingDefaultDeserializer = true;
					
					containedCols.add(readingExcelColumn);
					if(colCfg.required) requiredCols.add(readingExcelColumn);
					
					ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(readingExcelColumn);
					matchers.add(usingMatcher);
					MATCHER_TO_EXCEL_COLUMN.put(usingMatcher, readingExcelColumn);
				}
			}
			
			lookingClass = lookingClass.getSuperclass();
		}
		
		Set<ReadingExcelColumn> detectedColumns = new HashSet<>();
		ExcelTable excelTable = ExcelUtils.getOrUseDefaultExcelTableAnnotation(dataType);
		int titleNum = excelTable.titleAt();
		Row titleRow = ExcelUtils.getOrCreateRow(sheet, titleNum);
		for(int colNum = titleRow.getFirstCellNum(), lastColNum = titleRow.getLastCellNum(); colNum >= 0 && colNum < lastColNum; colNum++) {
			Cell cell = titleRow.getCell(colNum);
			if(cell == null) continue;
			
			String colName = ExcelUtils.getCellValueAsString(cell);
			for(ExcelColumnMatcher matcher : matchers) {
				if(matcher.isMatchWith(colName)) {
					ReadingExcelColumn col = MATCHER_TO_EXCEL_COLUMN.get(matcher);
					if(detectedColumns.contains(col)) throw new RuntimeException("将列名" + colName + "匹配到已完成匹配的列 " + col.colName + " 中");
					
					col.colIdx = colNum;
					col.colName = colName;
					detectedColumns.add(col);
					requiredCols.remove(col);
				}
			}
		}
		
		if(requiredCols.size() > 0) {
			StringBuilder requiredColsNameBuilder = new StringBuilder();
			for(ReadingExcelColumn col : requiredCols) requiredColsNameBuilder.append(col.columnConfiguration.name).append(", ");
			throw new RuntimeException("Excel表 " + sheet.getSheetName() + " 需要包括列 " + requiredColsNameBuilder.substring(0, requiredColsNameBuilder.length() - 2));
		}
		containedCols = null;
		requiredCols = null;
		matchers = null;
		
		// Read data
		ExcelValueDeserializerParameter deserializerParam = new ExcelValueDeserializerParameter();
		List<DataType> data = new ArrayList<>();
		for(int rowNum = sheet.getFirstRowNum(), lastRowNum = sheet.getLastRowNum(); rowNum >= 0 && rowNum <= lastRowNum; rowNum++) {
			Row row = sheet.getRow(rowNum);
			if(row == null || rowNum == titleNum) continue;
			
			DataType instance;
			try {
				instance = dataType.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("无法实例化", e);
			}
			
			boolean addInstanceToList = false;
			for(ReadingExcelColumn col : detectedColumns) {
				Cell cell = row.getCell(col.colIdx);
				if(cell == null || (cell.getCellType() == Cell.CELL_TYPE_BLANK && col.isUsingDefaultDeserializer)) continue;
				
				deserializerParam.rowIdx = rowNum;
				deserializerParam.colIdx = col.getColIdx();
				deserializerParam.readingColumn = col;
				deserializerParam.cell = cell;
				Object value = col.deserializer.deserialize(deserializerParam);
				col.valueSetter.set(col, value, instance);
				
				addInstanceToList = true;
			}
			
			if (addInstanceToList) data.add(instance);
		}
		
		return data;
	}
	
	private static ExcelColumnMatcher getExcelColumnMatcher(ReadingExcelColumn readingExcelColumn) {
		ExcelColumnMatcher matcher;
		String determiningStr = null;
		ExcelColumnConfiguration configuration = readingExcelColumn.columnConfiguration;
		if((determiningStr = configuration.namePattern) != null && determiningStr.trim().length() > 0) {
			matcher = new ExcelColumnPatternMatcher(determiningStr);
		}else {
			if((determiningStr = configuration.name) == null || determiningStr.trim().length() <= 0) {
				if(readingExcelColumn.annotatedObject instanceof Field)
					determiningStr = ((Field)readingExcelColumn.annotatedObject).getName();
				else determiningStr = resolvePropertyNameFromMethod((Method) readingExcelColumn.annotatedObject);
			}
			matcher = new ExcelColumnFullNameMatcher(determiningStr);
		}
		return matcher;
	}
	
	/**
	 * 根据方法名确定属性名
	 */
	private static String resolvePropertyNameFromMethod(Method method) {
		String name = method.getName();
		if(name.startsWith("set")) {
			name = name.substring(3);
			name = name.substring(0, 1).toLowerCase().concat(name.substring(1));
		}
		return name;
	}
	
	public static Set<String> containSheets(Workbook wb, String...names) {
		Set<String> set = new HashSet<>();
		if(names != null && names.length > 0) {
			for(String name : names) {
				if(name != null) {
					for(int i = 0; i < wb.getNumberOfSheets(); i++) {
						if(wb.getSheetName(i).indexOf(name) >= 0) {
							set.add(name);
							break;
						}
					}
				}
			}
		}
		return set;
	}
}
