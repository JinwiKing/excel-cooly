package org.king.excooly.support.poi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.king.excooly.DefaultExcelRowValueWriter;
import org.king.excooly.ExcelAssistant;
import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.ExcelColumnFullNameMatcher;
import org.king.excooly.ExcelColumnMatcher;
import org.king.excooly.ExcelColumnPatternMatcher;
import org.king.excooly.ExcelCooly;
import org.king.excooly.ExcelCoolyConfiguration;
import org.king.excooly.ExcelRowValueWriter;
import org.king.excooly.ExcelTable;
import org.king.excooly.ExcelType;
import org.king.excooly.ExcelUtils;
import org.king.excooly.ExcelWriter;
import org.king.excooly.UsingDefaultValueGetter;
import org.king.excooly.UsingDefaultValueSerializer;
import org.king.excooly.logger.Logger;
import org.king.excooly.logger.LoggerFactory;
import org.king.excooly.support.JavaBeanMethodGetter;
import org.king.excooly.support.JavaBeanMethorSetter;
import org.king.excooly.support.JavaPropertyValueInjector;
import org.king.excooly.support.JavaProprtyValueExtractor;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.PropertyValueDynamicSerializer;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.util.ReflectionUtils;

/**
 * 
 * @author king 
 */
public class PoiExcelCooly implements ExcelCooly {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ExcelAssistant excelVailator;

	@Override
	public <Type> List<Type> read(InputStream is, String sheetName, Class<Type> dataType, ExcelCoolyConfiguration configuration) {
		ExcelType excelType;
		if((excelType = excelVailator.detectExcelTypeFromStream(is)) == ExcelType.UNKNOWN) {
			throw new IllegalArgumentException("Given input stream is not an excel input stream");
		}
		
		Workbook workbook;
		try {
			if(excelType == ExcelType.XLS) workbook = new HSSFWorkbook(is);
			else workbook = new XSSFWorkbook(is);
		} catch (Exception e) {
			throw new RuntimeException("Can not opne work book", e);
		}
		
		// 数据处理步骤
		// excel值 -> 反序列化excel值为java值 -> 由java值注入器注入到java bean中
		
		try {
			Sheet sheet = workbook.getSheet(sheetName);
			if(sheet == null) return new ArrayList<>(0);
			Class<?> lookingClass = dataType;
			Set<ReadingExcelColumn> containedCols = new HashSet<>(), requiredCols = new HashSet<>();
			Set<ExcelColumnMatcher> matchers = new HashSet<>();
			Map<Class<?>, ExcelCellValueDeserializer> deserializers = configuration.getCellValueDeserializers();
			Map<ExcelColumnMatcher, ReadingExcelColumn> matcherExcelColumnMap = new HashMap<>();
			while(lookingClass != Object.class) {
				
				// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
				// property or a method of an instance, so we need to scan all properties 
				// and all method of the instance.
				
				// On field
				for(Field field : lookingClass.getDeclaredFields()) {
					ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(field);
					if(colCfg != null && !colCfg.ignoreDeserialization) {
						
						// 获取反序列化器
						Class<?> limitedType = null;
						ExcelCellValueDeserializer deserializer = null;
						Method javaBeanSetter = null;
						if(!colCfg.deserializerType.equals(UsingDefaultValueDeserializer.class)) {
							try {
								deserializer = colCfg.deserializerType.newInstance();
							} catch (Exception e) {
								throw new RuntimeException("无法实例化属性设置器", e);
							}
						}else {
							
							// According to the type of field or method to choice a deserializerType if there are not given deserializerType

							javaBeanSetter = ReflectionUtils.getAccessibleSetterOfField(lookingClass, field, field.getType());
							if(javaBeanSetter == null) javaBeanSetter = ReflectionUtils.getAccessibleSetterOfField(lookingClass, field);
							if(javaBeanSetter == null) limitedType = field.getType();
							deserializer = deserializers.get(limitedType);
						}
						colCfg.deserializer = deserializer;
						
						// 决定java值注入器。如果指定了注入器，则使用指定的注入器，否则，如果指定了反序列化器，则认为该反序列化的返回值符合对应属性的类型；因为默认的反序列化器已经反序列化到指定
						// 的类型，所以直接查找对应类型的setter或直接注入即可
						JavaValueSetter valueSetter = null;
						if(!colCfg.valueSetterType.equals(UsingDefaultValueSetter.class)) {
							try {
								valueSetter = colCfg.valueSetterType.newInstance();
							} catch (Exception e) {
								throw new RuntimeException("无法实例化属性注入器", e);
							}
						}else {
							if(javaBeanSetter == null) {
								field.setAccessible(true);
								valueSetter = new JavaPropertyValueInjector(field);
							}else valueSetter = new JavaBeanMethorSetter(javaBeanSetter);
						}
						colCfg.valueSetter = valueSetter;
						
						ReadingExcelColumn readingExcelColumn = new ReadingExcelColumn(colCfg);
						
						containedCols.add(readingExcelColumn);
						if(colCfg.required) requiredCols.add(readingExcelColumn);
						
						ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(colCfg);
						matchers.add(usingMatcher);
						matcherExcelColumnMap.put(usingMatcher, readingExcelColumn);
					}
				}
				
				// On method
				for(Method method : lookingClass.getDeclaredMethods()) {
					ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(method);
					if(colCfg != null && !colCfg.ignoreDeserialization) {
						int numParams = method.getParameterCount();
						if(numParams == 0) continue;	// May be a getter
						else if(numParams != 1) throw new RuntimeException("请确保使用用于设置Excel值的方法只接受一个参数");
						
						Class<?> limitedType = method.getParameterTypes()[0];
						ExcelCellValueDeserializer deserializer = deserializers.get(limitedType);
						colCfg.deserializer = deserializer;

						ReadingExcelColumn readingExcelColumn = new ReadingExcelColumn(colCfg);
						
						containedCols.add(readingExcelColumn);
						if(colCfg.required) requiredCols.add(readingExcelColumn);
						
						ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(colCfg);
						matchers.add(usingMatcher);
						matcherExcelColumnMap.put(usingMatcher, readingExcelColumn);
					}
				}
				
				lookingClass = lookingClass.getSuperclass();
			}
			
			Set<ReadingExcelColumn> matchedColumns = new HashSet<>();
			ExcelTable excelTable = getOrUseDefaultExcelTableAnnotation(dataType);
			int titleNum = excelTable.titleAt();
			Row titleRow = ExcelUtils.getOrCreateRow(sheet, titleNum);
			for(int colNum = titleRow.getFirstCellNum(), lastColNum = titleRow.getLastCellNum(); colNum >= 0 && colNum < lastColNum; colNum++) {
				Cell cell = titleRow.getCell(colNum);
				if(cell == null) continue;
				
				String colName = ExcelUtils.getCellValueAsString(cell);
				for(ExcelColumnMatcher matcher : matchers) {
					if(matcher.isMatchWith(colName)) {
						ReadingExcelColumn col = matcherExcelColumnMap.get(matcher);
						if(matchedColumns.contains(col)) throw new RuntimeException("将列名" + colName + "匹配到已完成匹配的列 " + col.colName + " 中");
						
						col.colIdx = colNum;
						col.colName = colName;
						matchedColumns.add(col);
						requiredCols.remove(col);
					}
				}
			}
			
			if(requiredCols.size() > 0) {
				StringBuilder requiredColsNameBuilder = new StringBuilder();
				for(ReadingExcelColumn col : requiredCols) requiredColsNameBuilder.append(col.configuration.name).append(", ");
				throw new RuntimeException("Excel表 " + sheet.getSheetName() + " 需要包括列 " + requiredColsNameBuilder.substring(0, requiredColsNameBuilder.length() - 2));
			}
			containedCols = null;
			requiredCols = null;
			matchers = null;
			
			// Read data
			PoiExcelValueDeserializerParameter deserializerParam = new PoiExcelValueDeserializerParameter();
			List<Type> data = new ArrayList<>();
			for(int rowNum = sheet.getFirstRowNum(), lastRowNum = sheet.getLastRowNum(); rowNum >= 0 && rowNum <= lastRowNum; rowNum++) {
				Row row = sheet.getRow(rowNum);
				if(row == null || rowNum == titleNum) continue;
				
				Type instance;
				try {
					instance = dataType.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("无法实例化", e);
				}
				
				boolean addInstanceToList = false;
				for(ReadingExcelColumn col : matchedColumns) {
					Cell cell = row.getCell(col.colIdx);
					
					deserializerParam.rowIdx = rowNum;
					deserializerParam.cell = cell;
					deserializerParam.readingColumn = col;
					Object value = col.configuration.deserializer.deserialize(deserializerParam);
					col.configuration.valueSetter.set(instance, value, col);
					
					addInstanceToList = true;
				}
				
				if (addInstanceToList) data.add(instance);
			}
			
			return data;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				// don't care
			}
		}
	}
	
	private static ExcelColumnMatcher getExcelColumnMatcher(ExcelColumnConfiguration configuration) {
		ExcelColumnMatcher matcher;
		String determiningStr = null;
		if((determiningStr = configuration.namePattern) != null && determiningStr.trim().length() > 0) {
			matcher = new ExcelColumnPatternMatcher(determiningStr);
		}else {
			if((determiningStr = configuration.name) == null || determiningStr.trim().length() <= 0) {
				if(configuration.accessibleObject instanceof Field) {
					determiningStr = ((Field)configuration.accessibleObject).getName();
				}else {
					determiningStr = ((Method) configuration.accessibleObject).getName();
					if(determiningStr.startsWith("set")) {
						determiningStr = determiningStr.substring(3);
						determiningStr = determiningStr.substring(0, 1).toLowerCase().concat(determiningStr.substring(1));
					}
				}
			}
			matcher = new ExcelColumnFullNameMatcher(determiningStr);
		}
		return matcher;
	}
	
	private static ExcelTable getOrUseDefaultExcelTableAnnotation(Class<?> dataType) {
		ExcelTable excelTable = dataType.getAnnotation(ExcelTable.class);
		if(excelTable == null) excelTable = ExcelWriter.class.getDeclaredAnnotation(ExcelTable.class);
		return excelTable;
	}
	
	public static void write(Workbook wb, String sheetName, List<?> data, boolean override) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null) throw new IllegalArgumentException("No such sheet named " + sheetName);
		
		// java值 -> java值获取器将值读取出来 -> java值序列化器将值序列化到excel中
		
		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		Class<?> dataType = getDataTypeFromData(data);
		if(dataType != null) {
			int rowNo = override ? 0 : sheet.getLastRowNum() + 1;
			
			List<WritingExcelColumn> containedFields = new ArrayList<>();
			Class<?> lookingClass = dataType;
			while(lookingClass != Object.class) {
				
				// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
				// property or a method of an instance, so we need to scan all properties 
				// and all method of the instance.
				
				// On property
				for(Field field : lookingClass.getDeclaredFields()) {
					ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(field);
					if (colCfg != null) {
						
						// 决定将要使用的属性值获取器
						Method javaBeanGetter = null;
						JavaValueGetter valueGetter = null;
						if(!colCfg.valueGetterType.equals(UsingDefaultValueGetter.class)) {
							try {
								valueGetter = colCfg.valueGetterType.newInstance();
							} catch (Exception e) {
								throw new RuntimeException("无法实例化属性获取器", e);
							}
						}else {
							javaBeanGetter = ReflectionUtils.getAccessibleGetterOfField(lookingClass, field);
							if(javaBeanGetter == null) {
								field.setAccessible(true);
								valueGetter = new JavaProprtyValueExtractor(field);
							}else valueGetter = new JavaBeanMethodGetter(javaBeanGetter);
						}
						
						PropertyValueSerializer serializer = null;
						if(!colCfg.serializerType.equals(UsingDefaultValueSerializer.class)) {
							try {
								serializer = colCfg.serializerType.newInstance();
							} catch (Exception e) {
								throw new RuntimeException("无法实例化序列化器", e);
							}
						}else serializer = new PropertyValueDynamicSerializer(new HashMap<>());
						colCfg.serializer = serializer;
						
						containedFields.add(new WritingExcelColumn(colCfg, field, javaBeanGetter, valueGetter, serializer));
					}
				}
				
				// On method
				for(Method method : lookingClass.getDeclaredMethods()) {
					ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(method);
					if (colCfg != null) {
						
						// Make sure that the method which will be call for getting value to 
						// serialize to excel table is a non-parameter method. 
						if(method.getReturnType().equals(Void.TYPE)) continue;	// May be a setter
						else if (method.getParameterCount() > 0) throw new RuntimeException("请确保将返回值序列化到Excel表内的方法为无参方法");
						
						if (!method.isAccessible()) method.setAccessible(true);
						JavaValueGetter getter = new JavaBeanMethodGetter(method);
						
						PropertyValueSerializer serializer = null;
						if(!colCfg.serializerType.equals(UsingDefaultValueSerializer.class)) {
							try {
								serializer = colCfg.serializerType.newInstance();
							} catch (Exception e) {
								throw new RuntimeException("无法实例化序列化器", e);
							}
						}else serializer = new PropertyValueDynamicSerializer(new HashMap<>());
						
						containedFields.add(new WritingExcelColumn(colCfg, method, method, getter, serializer));
					}
				}
				
				lookingClass = lookingClass.getSuperclass();
			}
			containedFields.sort((f1, f2) -> f1.order - f2.order);
			
			ExcelTable excelTable = getOrUseDefaultExcelTableAnnotation(dataType);
			int numFieldsNeedToWrite = containedFields.size();
			ExcelRowValueWriter rowValueWriter = DefaultExcelRowValueWriter.Singleton.INSTANCE;
			if(!excelTable.rowValueWriter().equals(DefaultExcelRowValueWriter.class)) {
				try {
					rowValueWriter = excelTable.rowValueWriter().newInstance();
				} catch (Exception e) {
					throw new RuntimeException("无法实例化Excel行值写入器", e);
				}
			}
			
			// Set column width and write title
			// Note: Only when it is in the override mode, it sets the column width
			CellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(excelTable.titleAlignment());
			titleStyle.setVerticalAlignment(excelTable.titleVerticalAlignment());
			titleStyle.setBorderTop(excelTable.titleBorderTop());
			titleStyle.setBorderBottom(excelTable.titleBorderBottom());
			titleStyle.setBorderLeft(excelTable.titleBorderLeft());
			titleStyle.setBorderRight(excelTable.titleBorderRight());
			titleStyle.setWrapText(excelTable.titleWrapText());
			Row row = ExcelUtils.getOrCreateRow(sheet, rowNo++);
			row.setHeight(excelTable.titleHeight());
			for(int i = 0; i < numFieldsNeedToWrite; i++) {
				WritingExcelColumn writingExcelCell = containedFields.get(i);
				Cell cell = ExcelUtils.getOrCreateCell(row, i);
				cell.setCellStyle(titleStyle);
				cell.setCellValue(writingExcelCell.usingColName);
			}
			if(override) for(int i = 0; i < numFieldsNeedToWrite; i++) sheet.setColumnWidth(i, containedFields.get(i).width);
			
			// Write data
			ExcelRowValueWritingParam rowValueWritingParam = new ExcelRowValueWritingParam();
			rowValueWritingParam.excelTable = excelTable;
			rowValueWritingParam.workbook = wb;
			rowValueWritingParam.sheet = sheet;
			rowValueWritingParam.writingExcelColumns = containedFields;
			int numDataNeedToWrite = data.size();
			for(int i = 0; i < numDataNeedToWrite; i++) {
				Object writingData = data.get(i);
				rowValueWritingParam.instance = writingData;
				rowValueWritingParam.writeToLineNo = rowNo;
				rowNo = rowValueWriter.write(rowValueWritingParam);
			}
		}
	}
	
	private static Class<?> getDataTypeFromData(List<?> data){
		Iterator<?> iterator = data.iterator();
		Object instance = null;
		while(instance == null && iterator.hasNext()) instance = iterator.next();
		return instance == null ? null : instance.getClass();
	}
}
