package org.king.excooly.support.poi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.ExcelCooly;
import org.king.excooly.ExcelRowValueWriter;
import org.king.excooly.ExcelTable;
import org.king.excooly.ExcelType;
import org.king.excooly.UsingDefaultValueGetter;
import org.king.excooly.UsingDefaultValueSerializer;
import org.king.excooly.logger.Logger;
import org.king.excooly.logger.LoggerFactory;
import org.king.excooly.support.ExcelColumnMatcher;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.common.ExcelColumnFullNameMatcher;
import org.king.excooly.support.common.ExcelColumnPatternMatcher;
import org.king.excooly.support.common.ExcelUtils;
import org.king.excooly.support.common.JavaBeanMethodGetter;
import org.king.excooly.support.common.JavaBeanMethorSetter;
import org.king.excooly.support.common.JavaPropertyValueInjector;
import org.king.excooly.support.common.JavaProprtyValueExtractor;
import org.king.excooly.support.common.MergedColumn;
import org.king.excooly.support.common.MergedList;
import org.king.excooly.support.common.MergedRow;
import org.king.excooly.support.common.PropertyValueDynamicSerializer;
import org.king.excooly.support.common.ReflectionUtils;

/**
 * 
 * @author king 
 */
@ExcelTable
public class PoiExcelCooly implements ExcelCooly {
	private static final Set<Class<?>> JDK_VALUE_TYPE = new HashSet<>();
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<Class<?>, ExcelCellValueDeserializer> deserializers = new HashMap<>();
	private final Map<Class<?>, PropertyValueSerializer> serializers = new HashMap<>();
	
	static {
		JDK_VALUE_TYPE.add(byte.class);
		JDK_VALUE_TYPE.add(Byte.class);
		JDK_VALUE_TYPE.add(short.class);
		JDK_VALUE_TYPE.add(Short.class);
		JDK_VALUE_TYPE.add(int.class);
		JDK_VALUE_TYPE.add(Integer.class);
		JDK_VALUE_TYPE.add(long.class);
		JDK_VALUE_TYPE.add(Long.class);
		JDK_VALUE_TYPE.add(float.class);
		JDK_VALUE_TYPE.add(Float.class);
		JDK_VALUE_TYPE.add(double.class);
		JDK_VALUE_TYPE.add(Double.class);
		JDK_VALUE_TYPE.add(String.class);
		JDK_VALUE_TYPE.add(Date.class);
		JDK_VALUE_TYPE.add(BigDecimal.class);
	}
	
	public PoiExcelCooly (Map<Class<?>, ExcelCellValueDeserializer> deserializes, Map<Class<?>, PropertyValueSerializer> serializers) {
		if(deserializes != null) this.deserializers.putAll(deserializes);
		if(serializers != null) this.serializers.putAll(serializers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Type> List<Type> read(InputStream is, String sheetName, Class<Type> dataType) {
		if(is == null) return new ArrayList<>(0);
		
		if(!is.markSupported()) is = new BufferedInputStream(is);
		ExcelType excelType;
		if((excelType = ExcelUtils.detectExcelTypeFromStream(is)) == ExcelType.UNKNOWN) {
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
			LinkedList<Class<?>> lookingClassStack = new LinkedList<>();
			LinkedList<Class<?>> dataTypeStack = new LinkedList<>();
			lookingClassStack.add(dataType);
			dataTypeStack.add(dataType);
			List<ExcelColumnConfiguration> colCfgs = readConfigurationFromClass(dataType, dataType);
			List<FocusingExcelColumn> basicCols = prepareFocusingExcelColumn(colCfgs);
			Queue<FocusingExcelColumn> containedCols = new LinkedList<>(basicCols);
			Set<FocusingExcelColumn> requiredCols = new HashSet<>();
			Set<ExcelColumnMatcher> matchers = new HashSet<>();
			Map<ExcelColumnMatcher, FocusingExcelColumn> matcherExcelColumnMap = new HashMap<>();
			while(!containedCols.isEmpty()) {
				FocusingExcelColumn column = containedCols.remove();
				ExcelColumnConfiguration colCfg = column.configuration;
				
				ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(colCfg);
				matchers.add(usingMatcher);
				matcherExcelColumnMap.put(usingMatcher, column);
				
				if(colCfg.isEmbedded) {
					column.isEmbedded = colCfg.isEmbedded;
					containedCols.addAll(column.embeddedExcelColumns);
				}else if(colCfg.requiredForDeserializing) requiredCols.add(column);
			}

			ExcelTable excelTable = getOrUseDefaultExcelTableAnnotation(dataType);
			int titleNum = excelTable.titleAt();
			Row titleRow = ExcelUtils.getOrCreateRow(sheet, titleNum);
			{
				Set<FocusingExcelColumn> matchedColumns = new HashSet<>();
				for(int colNum = titleRow.getFirstCellNum(), lastColNum = titleRow.getLastCellNum(); colNum >= 0 && colNum < lastColNum; colNum++) {
					Cell cell = titleRow.getCell(colNum);
					if(cell == null) continue;
					
					String colName = ExcelUtils.getCellValueAsString(cell);
					for(ExcelColumnMatcher matcher : matchers) {
						if(matcher.isMatchWith(colName)) {
							FocusingExcelColumn col = matcherExcelColumnMap.get(matcher);
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
					for(FocusingExcelColumn col : requiredCols) requiredColsNameBuilder.append(col.configuration.name).append(", ");
					throw new RuntimeException("Excel表 " + sheet.getSheetName() + " 需要包括列 " + requiredColsNameBuilder.substring(0, requiredColsNameBuilder.length() - 2));
				}
				containedCols = null;
				requiredCols = null;
				matchers = null;
				matchedColumns = null;
			}
			
			// Read data
			MergedList<MergedRow> mergedRows = new MergedList<>();
			List<CellRangeAddress> addresses = sheet.getMergedRegions();
			if((addresses = sheet.getMergedRegions()) != null && addresses.size() > 0) {
				for(CellRangeAddress address : addresses) {
					int rowStartAt = address.getFirstRow();
					MergedRow mergedRow = mergedRows.get(rowStartAt);
					if(mergedRow == null) {
						mergedRow = new MergedRow(rowStartAt, address.getLastRow() - address.getFirstRow() + 1);
						mergedRows.add(mergedRow);
					}
					int colStartAt = address.getFirstColumn();
					mergedRow.addMergedColumn(new MergedColumn(colStartAt, address.getLastColumn() - address.getFirstColumn() + 1));
				}
			}
			return extractData(mergedRows, basicCols, dataType, sheet, sheet.getFirstRowNum(), sheet.getLastRowNum(), titleNum);
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				// don't care
			}
		}
	}
	
	private List<ExcelColumnConfiguration> readConfigurationFromClass(Class<?> dataClass, Class<?> lookingClass){
		List<ExcelColumnConfiguration> colCfgs = new LinkedList<>();
		while(lookingClass != Object.class) {
			// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
			// property or a method of an instance, so we need to scan all properties 
			// and all method of the instance.
			
			// On field
			for(Field field : lookingClass.getDeclaredFields()) {
				ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(field);
				if(colCfg == null) continue;
				colCfgs.add(colCfg);
				colCfg.dataType = dataClass;
				
				// 获取反序列化器
				ExcelCellValueDeserializer deserializer = null;
				if(!colCfg.deserializerType.equals(UsingDefaultValueDeserializer.class)) {
					try {
						deserializer = colCfg.deserializerType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性设置器", e);
					}
				}else {
					// According to the type of field or method to choice a deserializerType if there are not given deserializerType
					
					Class<?> usingType = field.getType();
					boolean isArray = false;
					deserializer = deserializers.get(usingType);
					if(colCfg.isContainer || (isArray = colCfg.isArray)) {
						usingType = colCfg.concreteType;
						if(!JDK_VALUE_TYPE.contains(usingType)) {
							// 内嵌
							colCfg.dataType = usingType;
							colCfg.isEmbedded = true;
							colCfg.embeddedConfigurations = readConfigurationFromClass(usingType, usingType);
							deserializer = EmbeddedDeserializer.INSTANCE;
						}else if(deserializers.containsKey(usingType)) {
							if(isArray) deserializer = deserializers.get(ArrayCodec.class);
						}else deserializer = null;
					}
					
					if(deserializer == null) {
						throw new RuntimeException("No suitable deserializer for deserializing excel column value to field named " + field.getName());
					}
				}
				colCfg.deserializer = deserializer;
				
				// 决定java值注入器。如果指定了注入器，则使用指定的注入器，否则，如果指定了反序列化器，则认为该反序列化的返回值符合对应属性的类型；因为默认的反序列化器已经反序列化到指定
				// 的类型，所以直接查找对应类型的setter或直接注入即可
				Method javaBeanSetter = ReflectionUtils.getAccessibleSetterOfField(lookingClass, field, field.getType());
				if(!colCfg.valueSetterType.equals(UsingDefaultValueSetter.class)) {
					try {
						colCfg.valueSetter = colCfg.valueSetterType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性注入器", e);
					}
				}else {
					if(javaBeanSetter == null) {
						field.setAccessible(true);
						colCfg.valueSetter = new JavaPropertyValueInjector(field);
					}else colCfg.valueSetter = new JavaBeanMethorSetter(javaBeanSetter);
				}
			}
			
			// On method
			for(Method method : lookingClass.getDeclaredMethods()) {
				ExcelColumnConfiguration colCfg = PoiExcelColumnConfigurationBuilder.buildFrom(method);
				if(colCfg != null) {
					colCfgs.add(colCfg);
					
					int numParams = method.getParameterCount();
					if(numParams == 0) continue;	// May be a getter
					else if(numParams != 1) throw new RuntimeException("请确保使用用于设置Excel值的方法只接受一个参数");
					
					Class<?> limitedType = method.getParameterTypes()[0];
					ExcelCellValueDeserializer deserializer = deserializers.get(limitedType);
					colCfg.deserializer = deserializer;
				}
			}
			
			lookingClass = lookingClass.getSuperclass();
		}
		
		return colCfgs;
	}
	
	private List<FocusingExcelColumn> prepareFocusingExcelColumn(List<ExcelColumnConfiguration> colCfgs) {
		List<FocusingExcelColumn> columns = new LinkedList<>();
		for(ExcelColumnConfiguration colCfg : colCfgs) {
			FocusingExcelColumn focusingExcelColumn = new FocusingExcelColumn(colCfg);
			columns.add(focusingExcelColumn);
			if(colCfg.isEmbedded) {
				focusingExcelColumn.isEmbedded = true;
				focusingExcelColumn.embeddedExcelColumns = prepareFocusingExcelColumn(colCfg.embeddedConfigurations);
			}
		}
		return columns;
	}
	
	private static ExcelColumnMatcher getExcelColumnMatcher(ExcelColumnConfiguration configuration) {
		ExcelColumnMatcher matcher;
		String determiningStr = null;
		if((determiningStr = configuration.matchPattern) != null && determiningStr.trim().length() > 0) {
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
		if(excelTable == null) excelTable = PoiExcelCooly.class.getDeclaredAnnotation(ExcelTable.class);
		return excelTable;
	}
	
	// [startRowNum, endRowNum]
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List extractData(MergedList<MergedRow> mergedRows, List<FocusingExcelColumn> columns, Class<?> dataType, Sheet sheet, int startRowNum, int endRowNum, int titleNum) {
		PoiExcelValueDeserializerParameter deserializerParam = new PoiExcelValueDeserializerParameter();
		List data = new ArrayList();
		for(int rowNum = startRowNum, lastRowNum = endRowNum; rowNum >= 0 && rowNum <= lastRowNum; rowNum++) {
			if(rowNum == titleNum) continue;
			
			int nextRowNum = rowNum, numMergedRows = 0;
			MergedRow mergedRow = mergedRows.get(rowNum);
			if(mergedRow != null) {
				nextRowNum = rowNum + (numMergedRows = mergedRow.getNumRowMergeds()) - 1;
			}
			
			Row row = sheet.getRow(rowNum);
			if(row == null) {
				rowNum = nextRowNum;
				continue;
			}
			
			Object instance;
			try {
				instance = dataType.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("无法实例化", e);
			}
			
			boolean addInstanceToList = false;
			for(FocusingExcelColumn col : columns) {
				if(col.isEmbedded) {
					ExcelColumnConfiguration colCfg = col.configuration;
					List ls = extractData(mergedRows, col.embeddedExcelColumns, colCfg.dataType, sheet, rowNum, nextRowNum, titleNum);
					if(colCfg.isArray) {
						Object arr = Array.newInstance(dataType, ls.size());
						for(int i = 0, num = ls.size(); i < num; i++) Array.set(arr, i, ls.get(i));
						col.configuration.valueSetter.set(instance, arr, null);
					}else if(colCfg.isContainer) {
						col.configuration.valueSetter.set(instance, ls, null);
					}else {
						int num;
						if((num = ls.size()) <= 0) col.configuration.valueSetter.set(instance, null, null);
						else if(num <= 1) col.configuration.valueSetter.set(instance, ls.get(0), null);
						else throw new RuntimeException("无法将多行数据转为单行数据");
					}
					
					addInstanceToList = true;
				}else {
					int colIdx;
					Cell cell = row.getCell(colIdx = col.colIdx);
					if(cell == null) continue;
					
					Object[] cells = null;
					if(numMergedRows <= 1) cells = new Object[] {cell};
					else {
						cells = new Object[numMergedRows];
						cells[0] = cell;
						for(int i = 1, subRowNum = rowNum + 1, stopAt = rowNum + numMergedRows; subRowNum < stopAt; subRowNum++, i++) {
							Row subRow = sheet.getRow(subRowNum);
							if(subRow != null) cells[i] = subRow.getCell(colIdx);
						}
					}
					
					deserializerParam.rowIdx = rowNum;
					deserializerParam.cells = cells;
					deserializerParam.readingColumn = col;
					deserializerParam.deserializes = deserializers;
					deserializerParam.targetJavaType = col.configuration.concreteType;
					Object value = col.configuration.deserializer.deserialize(deserializerParam);
					col.configuration.valueSetter.set(instance, value, col);
					
					addInstanceToList = true;
				}
			}
			
			if (addInstanceToList) data.add(instance);
			
			rowNum = nextRowNum;
		}
		
		return data;
	}
	
	public void write(String fileName, String sheetName, List<?> data, boolean override) {
		if (fileName == null) throw new RuntimeException("File name must not be null");
		
		ExcelType excelType = ExcelType.XLSX;
		if(fileName.endsWith(".xls")) excelType = ExcelType.XLS;
		
		Workbook workbook;
		try {
			if(excelType == ExcelType.XLS) workbook = new HSSFWorkbook();
			else workbook = new XSSFWorkbook();
		} catch (Exception e) {
			throw new RuntimeException("Can not opne work book", e);
		}
		try {
			Sheet sheet = workbook.getSheet(sheetName);
			if(sheet == null) sheet = workbook.createSheet(sheetName);
			
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
							}else serializer = new PropertyValueDynamicSerializer(serializers);
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
							}else serializer = new PropertyValueDynamicSerializer(serializers);
							
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
				CellStyle titleStyle = workbook.createCellStyle();
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
				rowValueWritingParam.workbook = workbook;
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
			try {
				workbook.write(new BufferedOutputStream(new FileOutputStream(fileName)));
			} catch (Exception e) {
				throw new RuntimeException("Write excel failure", e);
			}
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				// don't care
			}
		}
	}
	
	private static Class<?> getDataTypeFromData(List<?> data){
		Iterator<?> iterator = data.iterator();
		Object instance = null;
		while(instance == null && iterator.hasNext()) instance = iterator.next();
		return instance == null ? null : instance.getClass();
	}
	
	private static class EmbeddedDeserializer implements ExcelCellValueDeserializer{
		static final EmbeddedDeserializer INSTANCE = new EmbeddedDeserializer();

		@Override
		public Object deserialize(ExcelValueDeserializerParameter deserializerParam) {
			return null;
		}
	}
}
