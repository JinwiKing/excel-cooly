package org.king.excool.poi;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.king.excool.*;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 
 * @author king 
 */
@ExcelTable
public class PoiExcelCooly implements ExcelOperator {
	private static final LfuCacheManager CACHE_MANAGER = new LfuCacheManager(128);
	private static final Set<Class<?>> JDK_VALUE_TYPE = new HashSet<>();
	private final Logger logger = Logger.getLogger(getClass());
	private final Map<Class<?>, ExcelCellValueDeserializer> deserializers = new HashMap<>();
	private final Map<Class<?>, PropertyValueSerializer> serializers = new HashMap<>();
	private final SimpleCellValueReader simpleCellValueReader;
	
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
		JDK_VALUE_TYPE.add(LocalDate.class);
		JDK_VALUE_TYPE.add(LocalTime.class);
		JDK_VALUE_TYPE.add(LocalDateTime.class);
		JDK_VALUE_TYPE.add(BigDecimal.class);
	}
	
	public PoiExcelCooly (Map<Class<?>, ExcelCellValueDeserializer> deserializes, Map<Class<?>, PropertyValueSerializer> serializers, SimpleCellValueReader simpleCellValueReader) {
		if(deserializes != null) this.deserializers.putAll(deserializes);
		if(serializers != null) this.serializers.putAll(serializers);
		this.simpleCellValueReader = simpleCellValueReader;
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
			
			DefaultExcelColumnConfigurationCollection rootCfgCollection = (DefaultExcelColumnConfigurationCollection) CACHE_MANAGER.get(dataType);
			if(rootCfgCollection == null) {
				rootCfgCollection = extractCfgFromClass(dataType, dataType);
				CACHE_MANAGER.add(dataType, rootCfgCollection);
			}
			DefaultReadingExcelColumnCollection rootColCollection = prepareReadingExcelColumn(rootCfgCollection);
			Queue<ReadingExcelColumnCollection> colCollectionQue = new LinkedList<>();
			colCollectionQue.add(rootColCollection);
			Set<ReadingExcelColumn> requiredCols = new HashSet<>();
			Map<ExcelColumnMatcher, ReadingExcelColumn> matcherExcelColumnMap = new HashMap<>();
			while(!colCollectionQue.isEmpty()) {
				DefaultReadingExcelColumnCollection colCollection = colCollectionQue.remove();
				for(DefaultReadingExcelColumn col : colCollection.columns) {
					DefaultExcelColumnConfiguration colCfg = col.columnConfiguration;
					
					if(!colCfg.isCascaded) {
						ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(colCfg);
						matcherExcelColumnMap.put(usingMatcher, col);
						if(colCfg.requiredForDeserializing) requiredCols.add(col);
					}else colCollectionQue.add(col.cascadeCollection);
				}
			}

			ExcelTable excelTable = getOrUseDefaultExcelTableAnnotation(dataType);
			int titleNum = excelTable.titleAt();
			Row titleRow = ExcelUtils.getOrCreateRow(sheet, titleNum);
			{
				Set<ReadingExcelColumn> matchedColumns = new HashSet<>();
				for(int colNum = titleRow.getFirstCellNum(), lastColNum = titleRow.getLastCellNum(); colNum >= 0 && colNum < lastColNum; colNum++) {
					Cell cell = titleRow.getCell(colNum);
					if(cell == null) continue;
					
					String colName = simpleCellValueReader.read(cell);
					for(ExcelColumnMatcher matcher : matcherExcelColumnMap.keySet()) {
						if(matcher.isMatchWith(colName)) {
							DefaultReadingExcelColumn col = matcherExcelColumnMap.get(matcher);
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
					for(DefaultReadingExcelColumn col : requiredCols) requiredColsNameBuilder.append(col.columnConfiguration.name).append(", ");
					throw new RuntimeException("Excel表 " + sheet.getSheetName() + " 需要包括列 " + requiredColsNameBuilder.substring(0, requiredColsNameBuilder.length() - 2));
				}
			}
			requiredCols = null;
			
			// Read data
			MergedAddressMap mergedAddressMap = new MergedAddressMap();
			List<CellRangeAddress> addresses = sheet.getMergedRegions();
			if((addresses = sheet.getMergedRegions()) != null && addresses.size() > 0) {
				for(CellRangeAddress address : addresses) {
					int rowStart = address.getFirstRow(), colStart = address.getFirstColumn();
					MergedAddress mergedAddr = new MergedAddress(address.getFirstRow(), address.getLastRow(), address.getFirstColumn(), address.getLastColumn());
					mergedAddressMap.put(rowStart, colStart, mergedAddr);
				}
			}
			return extractData(mergedAddressMap, rootColCollection, dataType, sheet, sheet.getFirstRowNum(), sheet.getLastRowNum(), titleNum);
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				// don't care
			}
		}
	}
	
	private DefaultExcelColumnConfigurationCollection extractCfgFromClass(Class<?> dataClass, Class<?> lookingClass){
		DefaultExcelColumnConfigurationCollection collection = new DefaultExcelColumnConfigurationCollection();
		collection.type = dataClass;
		List<DefaultExcelColumnConfiguration> allColCfgs = collection.configurations;
		
		while(lookingClass != Object.class) {
			// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
			// property or a method of an instance, so we need to scan all properties 
			// and all method of the instance.
			
			// On field
			for(Field field : lookingClass.getDeclaredFields()) {
				DefaultExcelColumnConfiguration colCfg = readFrom(field);
				if(colCfg == null) continue;
				colCfg.belongCollection = collection;
				
				if(colCfg.isIdCell) collection.idColumnConfiguration = colCfg;
				allColCfgs.add(colCfg);

				// 获取反序列化器
				Class<?> fieldType = field.getType();
				ExcelCellValueDeserializer deserializer = null;
				boolean isCascaded;
				if(isCascaded = colCfg.isCascaded) {
					Class<?> cascadeType = colCfg.concreteType;
					if(!JDK_VALUE_TYPE.contains(cascadeType)) {
						collection.cascadedCollections.add(colCfg.cascadeConfigurationCollection = extractCfgFromClass(cascadeType, cascadeType));
						deserializer = CascadeDeserializer.INSTANCE;
					}
				}else if(!colCfg.deserializerType.equals(UsingDefaultValueDeserializer.class)) {
					try {
						deserializer = colCfg.deserializerType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性设置器", e);
					}
				}else {
					if(colCfg.isArray || colCfg.isContainer) {
						if(colCfg.isArray) deserializer = deserializers.get(ArrayResolver.class);
						else deserializer = deserializers.get(fieldType);
						
						if(!JDK_VALUE_TYPE.contains(colCfg.concreteType)) deserializer = null;
					}else deserializer = deserializers.get(fieldType);
					
					if(deserializer == null) {
						throw new RuntimeException("No suitable deserializer for deserializing excel column value to field named " + field.getName());
					}
				}
				colCfg.deserializer = deserializer;
				
				// 级联对象也要提供setter
				
				// 决定java值注入器。如果指定了注入器，则使用指定的注入器，否则，如果指定了反序列化器，则认为该反序列化的返回值符合对应属性的类型；因为默认的反序列化器已经反序列化到指定
				// 的类型，所以直接查找对应类型的setter或直接注入即可
				Method javaBeanSetter = ReflectionUtils.getAccessibleSetterOfField(lookingClass, field, fieldType);
				if(!isCascaded && !colCfg.valueSetterType.equals(UsingDefaultPropertySetter.class)) {
					try {
						colCfg.valueSetter = colCfg.valueSetterType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性注入器", e);
					}
				}else {
					if(javaBeanSetter == null) {
						field.setAccessible(true);
						colCfg.valueSetter = new JavaPropertyPropertyInjector(field);
					}else colCfg.valueSetter = new JavaBeanMethorSetter(javaBeanSetter);
				}
				
				// 级联对象也要提供getter
				
				JavaPropertyGetter valueGetter = null;
				Class<? extends JavaPropertyGetter> valueGetterType;
				if(!isCascaded && !(valueGetterType = colCfg.valueGetterType).equals(UsingDefaultPropertyGetter.class)) {
					try {
						valueGetter = valueGetterType.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性值读取器", e);
					}
				}else {
					Method javaBeanGetter =  ReflectionUtils.getAccessibleGetterOfField(lookingClass, field, fieldType);
					if(javaBeanGetter == null) {
						field.setAccessible(true);
						valueGetter = new JavaProprtyPropertyExtractor(field);
					}else {
						valueGetter = new JavaBeanMethodGetter(javaBeanGetter);
					}
				}
				colCfg.valueGetter = valueGetter;
			}
			
			// On method
			for(Method method : lookingClass.getDeclaredMethods()) {
				DefaultExcelColumnConfiguration colCfg = readFrom(method);
				if(colCfg != null) {
					allColCfgs.add(colCfg);
					
					int numParams = method.getParameterCount();
					if(numParams == 0) continue;	// May be a getter
					else if(numParams != 1) throw new RuntimeException("请确保使用用于设置Excel值的方法只接受一个参数");
					
					Class<?> limitedType = method.getParameterTypes()[0];
					ExcelCellValueDeserializer deserializer = deserializers.get(limitedType);
					colCfg.deserializer = deserializer;
					
					JavaPropertyGetter valueGetter = null;
					Class<? extends JavaPropertyGetter> valueGetterType;
					if(!(valueGetterType = colCfg.valueGetterType).equals(UsingDefaultPropertyGetter.class)) {
						try {
							valueGetter = valueGetterType.newInstance();
						} catch (Exception e) {
							throw new RuntimeException("无法实例化属性值读取器", e);
						}
					}else valueGetter = new JavaBeanMethodGetter(method);
					colCfg.valueGetter = valueGetter;
				}
			}
			
			lookingClass = lookingClass.getSuperclass();
		}
		
		return collection;
	}
	
	private DefaultReadingExcelColumnCollection prepareReadingExcelColumn(DefaultExcelColumnConfigurationCollection configurationCollection) {
		DefaultReadingExcelColumnCollection collection = new DefaultReadingExcelColumnCollection();
		collection.configurationCollection = configurationCollection;
		List<ReadingExcelColumn> columns = collection.columns;
		for(DefaultExcelColumnConfiguration colCfg : configurationCollection.configurations) {
			DefaultReadingExcelColumn exCol = new DefaultReadingExcelColumn(colCfg);
			if(colCfg.isCascaded) {
				exCol.isCascaded = true;
				collection.cascadedCollections.add(
						exCol.cascadeCollection = prepareReadingExcelColumn(colCfg.cascadeConfigurationCollection));
			}
			columns.add(exCol);
		}
		return collection; 
	}
	
	private DefaultWritingExcelColumnCollection prepareWritingExcelColumn(DefaultExcelColumnConfigurationCollection configurationCollection) {
		DefaultWritingExcelColumnCollection collection = new DefaultWritingExcelColumnCollection();
		collection.configurationCollection = configurationCollection;
		List<WritingExcelColumn> columns = collection.columns;
		for(DefaultExcelColumnConfiguration colCfg : configurationCollection.configurations) {
			DefaultWritingExcelColumn exCol = new DefaultWritingExcelColumn(colCfg);
			if(colCfg.isCascaded) {
				exCol.isCascaded = true;
				collection.cascadedCollections.add(
						exCol.cascadeCollection = prepareWritingExcelColumn(colCfg.cascadeConfigurationCollection));
			}
			columns.add(exCol);
		}
		return collection; 
	}
	
	private static ExcelColumnMatcher getExcelColumnMatcher(DefaultExcelColumnConfiguration configuration) {
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
	private List extractData(MergedAddressMap mergedAddressMap, DefaultReadingExcelColumnCollection colCollection, Class<?> dataType, Sheet sheet, int startRowNum, int endRowNum, int titleNum) {
		DefaultReadingExcelColumn idColumn = colCollection.idColumn;
		DefaultExcelValueDeserializerParameter deserializerParam = new DefaultExcelValueDeserializerParameter();
		List data = new ArrayList();
		for(int rowNum = startRowNum, lastRowNum = endRowNum; rowNum >= 0 && rowNum <= lastRowNum; rowNum++) {
			if(rowNum == titleNum) continue;
			
			Row row = sheet.getRow(rowNum);
			if(row == null) continue;
			
			List<ReadingExcelColumn> columns = colCollection.columns;
			
			// If there specified an id column then use it to confirm how many rows data write to the data type, otherwise 
			// choose certain column as id column
			// [start, end]
			int start = rowNum, end = rowNum, idColNum;
			if(idColumn == null) {
				DefaultReadingExcelColumn aCol = null;
				for(DefaultReadingExcelColumn col : columns) {
					if(!col.isCascaded) {
						aCol = col;
						break;
					}
				}
				if(aCol != null) idColNum = aCol.colIdx;
				else idColNum = row.getFirstCellNum();
			}else idColNum = idColumn.colIdx;
			MergedAddress mAddr = mergedAddressMap.get(rowNum, idColNum);
			if(mAddr != null) end = mAddr.getEndRow();
			
			Object instance;
			try {
				instance = dataType.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("无法实例化", e);
			}
			
			boolean addInstanceToList = false;
			for(DefaultReadingExcelColumn col : colCollection.columns) {
				
				if(col.isCascaded) {
					DefaultExcelColumnConfiguration colCfg = col.columnConfiguration;
					List ls = extractData(mergedAddressMap, col.cascadeCollection, colCfg.concreteType, sheet, start, end, titleNum);
					if(colCfg.isArray) {
						Object arr = Array.newInstance(dataType, ls.size());
						for(int i = 0, num = ls.size(); i < num; i++) Array.set(arr, i, ls.get(i));
						col.columnConfiguration.valueSetter.set(instance, arr, null);
					}else if(colCfg.isContainer) {
						col.columnConfiguration.valueSetter.set(instance, ls, null);
					}else {
						int num;
						if((num = ls.size()) <= 0) col.columnConfiguration.valueSetter.set(instance, null, null);
						else if(num <= 1) col.columnConfiguration.valueSetter.set(instance, ls.get(0), null);
						else throw new RuntimeException("无法将多行数据转为单行数据");
					}
					
					addInstanceToList = true;
				}else {
					int colIdx;
					Cell cell = row.getCell(colIdx = col.colIdx);
					if(cell == null) continue;
					
					Object[] cells = null;
					if(start == end) cells = new Object[] {cell};
					else {
						int numRows = end - start + 1;
						cells = new Object[numRows];
						cells[0] = cell;
						for(int i = 1, subRowNum = start + 1, stopAt = end; subRowNum <= stopAt; subRowNum++, i++) {
							Row subRow = sheet.getRow(subRowNum);
							if(subRow != null) cells[i] = subRow.getCell(colIdx);
						}
					}
					
					deserializerParam.rowIdx = rowNum;
					deserializerParam.cells = cells;
					deserializerParam.readingColumn = col;
					deserializerParam.deserializes = deserializers;
					deserializerParam.targetJavaType = col.columnConfiguration.concreteType;
					Object value = col.columnConfiguration.deserializer.deserialize(deserializerParam);
					col.columnConfiguration.valueSetter.set(instance, value, col);
					
					addInstanceToList = true;
				}
			}
			
			if (addInstanceToList) data.add(instance);
			
			rowNum = end;
		}
		
		return data;
	}
	
	@Override
	public <Type> void write(ExcelType excelType, String fileName, String sheetName, List<Type> data, Class<Type> dataType, boolean override) {
		if (fileName == null) throw new RuntimeException("File name must not be null");
		
		Workbook workbook;
		try {
			if(excelType == ExcelType.XLS) workbook = new HSSFWorkbook();
			else workbook = new XSSFWorkbook();
		} catch (Exception e) {
			throw new RuntimeException("Can not opne work book", e);
		}
		try {
			Sheet sheet = workbook.getSheet(sheetName);
			if(sheet != null) {
				if(override) {
					int oldSheetIdx = workbook.getSheetIndex(sheetName);
					if(oldSheetIdx >= 0) workbook.removeSheetAt(oldSheetIdx);
					sheet = null;
				}
			}
			if(sheet == null) sheet = workbook.createSheet(sheetName);
			
			// java值 -> java值获取器将值读取出来 -> java值序列化器将值序列化到excel中
			
			data = Optional.ofNullable(data).orElse(Collections.emptyList());
			if(dataType != null) {
				int rowNo = override ? 0 : sheet.getLastRowNum() + 1;
				
				DefaultExcelColumnConfigurationCollection rootCfgCollection = (DefaultExcelColumnConfigurationCollection) CACHE_MANAGER.get(dataType);
				if(rootCfgCollection == null) {
					rootCfgCollection = extractCfgFromClass(dataType, dataType);
					CACHE_MANAGER.add(dataType, rootCfgCollection);
				}
				DefaultWritingExcelColumnCollection rootColCollection = prepareWritingExcelColumn(rootCfgCollection);
				Queue<WritingExcelColumnCollection> colCollectionQue = new LinkedList<>();
				colCollectionQue.add(rootColCollection);
				List<WritingExcelColumn> containedFields = new ArrayList<>();
				while(!colCollectionQue.isEmpty()) {
					DefaultWritingExcelColumnCollection colCollection = colCollectionQue.remove();
					for(DefaultWritingExcelColumn col : colCollection.columns) {
						DefaultExcelColumnConfiguration colCfg = col.columnConfiguration;
						if(!colCfg.requiredForSerializing) continue;
						
						if(!col.isCascaded) containedFields.add(col); 
						else colCollectionQue.add(col.cascadeCollection);
					}
				}
				containedFields.sort((f1, f2) -> f1.order - f2.order);
				for(int i = 0, numCols = containedFields.size(); i < numCols; i++) {
					containedFields.get(i).order = i;
				}
				
				ExcelTable excelTable = getOrUseDefaultExcelTableAnnotation(dataType);
				int numFieldsNeedToWrite = containedFields.size();
				
				// Set column width and write title
				// Note: Only when it is in the override mode, it sets the column width
//				CellStyle titleStyle = workbook.createCellStyle();
//				titleStyle.setAlignment(excelTable.titleAlignment());
//				titleStyle.setVerticalAlignment(excelTable.titleVerticalAlignment());
//				titleStyle.setBorderTop(excelTable.titleBorderTop());
//				titleStyle.setBorderBottom(excelTable.titleBorderBottom());
//				titleStyle.setBorderLeft(excelTable.titleBorderLeft());
//				titleStyle.setBorderRight(excelTable.titleBorderRight());
//				titleStyle.setWrapText(excelTable.titleWrapText());
				Row row = ExcelUtils.getOrCreateRow(sheet, rowNo++);
				row.setHeight(excelTable.titleHeight());
				for(int i = 0; i < numFieldsNeedToWrite; i++) {
					DefaultWritingExcelColumn writingExcelCell = containedFields.get(i);
					Cell cell = ExcelUtils.getOrCreateCell(row, i);
//					cell.setCellStyle(titleStyle);
					cell.setCellValue(writingExcelCell.columName);
				}
				
				// Write data
				for(int i = 0, numDataNeedToWrite = data.size(); i < numDataNeedToWrite; i++) {
					Object writingData = data.get(i);
					int numRowsUsed = writeRowData(rowNo, writingData, sheet, rootColCollection);
					rowNo += numRowsUsed;
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
	
	/**
	 * 
	 * @return last writing row sum(inclusive)
	 */
	@SuppressWarnings("rawtypes")
	private int writeRowData(int startRowNum, Object dataInstance, Sheet sheet, DefaultWritingExcelColumnCollection writingColumnCollection) {
		if(dataInstance == null) return startRowNum;
		
		List<int[]> rowRanges = new LinkedList<>();
		Set<Integer> writedSingleRowCols = new HashSet<>();
		Row row = ExcelUtils.getOrCreateRow(sheet, startRowNum);
		int numRowsUsing = 0;
		List<WritingExcelColumn> columns = writingColumnCollection.columns;
		for(DefaultWritingExcelColumn column : columns) {
			DefaultExcelColumnConfiguration colCfg = column.columnConfiguration;
			Object prop = colCfg.valueGetter.get(dataInstance, column);
			if(prop == null) continue;
			numRowsUsing = Math.max(numRowsUsing, 1);
			
			if(column.isCascaded) {
				DefaultWritingExcelColumnCollection cascaded = column.cascadeCollection;
				if(colCfg.isArray) {
					int lenArr = Array.getLength(prop);
					int cascadedStartRowNum = startRowNum, tmpCountRow = 0;
					for(int i = 0; i < lenArr; i++) {
						Object dataInArr = Array.get(prop, i);
						if(dataInArr == null) continue;
						int numSubRows = writeRowData(cascadedStartRowNum, dataInArr, sheet, cascaded);
						cascadedStartRowNum += numSubRows;
						tmpCountRow += numSubRows;
					}
					if(tmpCountRow > 1) rowRanges.add(new int[] {startRowNum, startRowNum + tmpCountRow - 1});
					numRowsUsing = Math.max(numRowsUsing, tmpCountRow);
				}else if(colCfg.isContainer) {
					Collection c = (Collection) prop;
					int cascadedStartRowNum = startRowNum, tmpCountRow = 0;
					for(Object dataInC : c) {
						if(dataInC == null) continue;
						int numSubRows = writeRowData(cascadedStartRowNum, dataInC, sheet, cascaded);
						cascadedStartRowNum += numSubRows;
						tmpCountRow += numSubRows;
					}
					if(tmpCountRow > 1) rowRanges.add(new int[] {startRowNum, startRowNum + tmpCountRow - 1});
					numRowsUsing = Math.max(numRowsUsing, tmpCountRow);
				}else {
					int tmpCountRow = writeRowData(startRowNum, prop, sheet, cascaded);
					if(tmpCountRow > 1) rowRanges.add(new int[] {startRowNum, startRowNum + tmpCountRow - 1});
					numRowsUsing = Math.max(numRowsUsing, tmpCountRow);
				}
			}else {
				int colIdx = column.getOrder();
				DefaultPropertyValueSerializationParameter parameter = new DefaultPropertyValueSerializationParameter();
				parameter.columnConfiguration = colCfg;
				if(colCfg.isArray) {
					Class<?> concreteType = colCfg.concreteType;
					int lenArr = Array.getLength(prop), tmpCountRow = 0;
					for(int i = 0; i < lenArr; i++) {
						Object dataInArr = Array.get(prop, i);
						if(dataInArr == null) continue;
						parameter.javaValue = dataInArr;
						PropertyValueSerializer serializer = serializers.get(concreteType);
						if(serializer == null) throw new RuntimeException("没有支持类型 " + concreteType + " 的序列化器");
						String valToWrite = serializer.serialize(parameter);
						if(valToWrite == null) continue;
						
						Row subRow = ExcelUtils.getOrCreateRow(sheet, startRowNum + i);
						Cell cell = ExcelUtils.getOrCreateCell(subRow, colIdx);
						cell.setCellValue(valToWrite);
						
						tmpCountRow += 1;
					}
					if(tmpCountRow > 1) rowRanges.add(new int[] {startRowNum, startRowNum + tmpCountRow - 1});
					numRowsUsing = Math.max(numRowsUsing, tmpCountRow);
				}else if(colCfg.isContainer) {
					Class<?> concreteType = colCfg.concreteType;
					Collection c = (Collection) prop;
					int i = 0, tmpCountRow = 0;
					for(Object dataInC : c) {
						if(dataInC == null) continue;
						parameter.javaValue = dataInC;
						PropertyValueSerializer serializer = serializers.get(concreteType);
						if(serializer == null) throw new RuntimeException("没有支持类型 " + concreteType + " 的序列化器");
						String valToWrite = serializer.serialize(parameter);
						if(valToWrite == null) continue;
						
						Row subRow = ExcelUtils.getOrCreateRow(sheet, startRowNum + i++);
						Cell cell = ExcelUtils.getOrCreateCell(subRow, colIdx);
						cell.setCellValue(valToWrite);
						
						tmpCountRow += 1;
					}
					if(tmpCountRow > 1) rowRanges.add(new int[] {startRowNum, startRowNum + tmpCountRow - 1});
					numRowsUsing = Math.max(numRowsUsing, tmpCountRow);
				}else {
					PropertyValueSerializer serializer = serializers.get(prop.getClass());
					if(serializer == null) throw new RuntimeException("没有支持类型 " + prop.getClass() + " 的序列化器");
					parameter.javaValue = prop;
					String valToWrite = serializer.serialize(parameter);
					if(valToWrite == null) continue;
					Cell cell = ExcelUtils.getOrCreateCell(row, colIdx);
					cell.setCellValue(valToWrite);
					writedSingleRowCols.add(colIdx);
				}
			}
		}
		
		if(numRowsUsing > 1) {
			for(int singleRowCol : writedSingleRowCols) {
				for(int[] rowRange : rowRanges) {
					CellRangeAddress cra = new CellRangeAddress(rowRange[0], rowRange[1], singleRowCol, singleRowCol);
					sheet.addMergedRegion(cra);
				}
			}
		}
		
		return numRowsUsing;
	}
	
	private static DefaultExcelColumnConfiguration readFrom(AccessibleObject ao) {
		DefaultExcelColumnConfiguration cfg = null;
		Object anno = null;
		if((anno = ao.getAnnotation(ExcelDateCell.class)) != null) {
			ExcelDateCell cell = (ExcelDateCell) anno;
			
			cfg = new DefaultExcelColumnConfiguration();
			cfg.requiredForDeserializing = cell.requiredForDeserializing();
			cfg.columnIndex = cell.columnIndex();
			cfg.columnName = cell.columnName();
			cfg.name = cell.name();
			cfg.matchPattern = cell.matchPattern();
			cfg.deserializerType = cell.deserializer();
			cfg.valueSetterType = cell.setter();
			cfg.requiredForSerializing = cell.requiredForSerializing();
			cfg.order = cell.order();
			cfg.valueGetterType = cell.getter();
			
			cfg.isDateCell = true;
			String dateFormat = cell.dateFormat();
			dateFormat = dateFormat == null ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
			cfg.dateFormat = dateFormat;
		}else if((anno = ao.getAnnotation(ExcelEnumCell.class)) != null) {
			ExcelEnumCell cell = (ExcelEnumCell) anno;
			
			cfg = new DefaultExcelColumnConfiguration();
			cfg.requiredForDeserializing = cell.requiredForDeserializing();
			cfg.columnIndex = cell.columnIndex();
			cfg.columnName = cell.columnName();
			cfg.name = cell.name();
			cfg.matchPattern = cell.matchPattern();
			cfg.deserializerType = cell.deserializer();
			cfg.valueSetterType = cell.setter();
			cfg.requiredForSerializing = cell.requiredForSerializing();
			cfg.order = cell.order();
			cfg.valueGetterType = cell.getter();
//			cfg.serializerType = column.serializer();
			
			cfg.isEnum = true;
			ExcelColumnEnum[] enums = cell.enums();
			Map<String, String> propertyExcelMap = new HashMap<>(), excelPropertyMap = new HashMap<>();
			cfg.propertyExcelMap = propertyExcelMap;
			cfg.excelPropertyMap = excelPropertyMap;
			for(ExcelColumnEnum oneEnum : enums) {
				String java = oneEnum.javaVal(), excel = oneEnum.excelVal();
				propertyExcelMap.put(java, excel);
				excelPropertyMap.put(excel, java);
			}
			String defaultVal = cell.defaultPropertyVal();
			defaultVal = defaultVal.equals("") && !cell.defaultPropertyValAsEmptyString() ? null : defaultVal;
			cfg.defaultPropertyVal = defaultVal;
			defaultVal = cell.defaultExcelVal();
			defaultVal = defaultVal.equals("") && !cell.defaultExcelValAsEmptyString() ? null : defaultVal;
			cfg.defaultExcelVal = defaultVal;
		}else if((anno = ao.getAnnotation(ExcelCell.class)) != null) {
			ExcelCell cell = (ExcelCell) anno;
			
			cfg = new DefaultExcelColumnConfiguration();
			cfg.isIdCell = cell.idCell();
			cfg.accessibleObject = ao;
			cfg.requiredForDeserializing = cell.requiredForDeserializing() || cell.idCell();
			cfg.columnIndex = cell.columnIndex();
			cfg.columnName = cell.columnName();
			cfg.name = cell.name();
			cfg.matchPattern = cell.matchPattern();
			cfg.deserializerType = cell.deserializer();
			cfg.valueSetterType = cell.setter();
			cfg.requiredForSerializing = cell.requiredForSerializing();
			cfg.order = cell.order();
			cfg.serializingName = cell.serializingName();
			cfg.valueGetterType = cell.getter();
			cfg.concreteType = cell.concreteType();
			
			if(ao instanceof Field) {
				Field field = (Field) ao;
				cfg.isContainer = Collection.class.isAssignableFrom(field.getType());
				boolean isArray = field.getType().isArray();
				cfg.isArray = isArray;
				if(isArray) cfg.concreteType = field.getType().getComponentType();
			}
		}else if((anno = ao.getAnnotation(ExcelCascadeCell.class)) != null) {
			ExcelCascadeCell cell = (ExcelCascadeCell) anno;
			
			cfg = new DefaultExcelColumnConfiguration();
			cfg.isCascaded = true;
			cfg.accessibleObject = ao;
			cfg.concreteType = cell.concreteType();
			cfg.requiredForSerializing = true;
			
			if(ao instanceof Field) {
				Field field = (Field) ao;
				cfg.isContainer = Collection.class.isAssignableFrom(field.getType());
				boolean isArray = field.getType().isArray();
				cfg.isArray = isArray;
				if(isArray) cfg.concreteType = field.getType().getComponentType();
			}
		}
		return cfg;
	}
	
	private static class CascadeDeserializer implements ExcelCellValueDeserializer{
		static final CascadeDeserializer INSTANCE = new CascadeDeserializer();

		@Override
		public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
			return null;
		}
	}
}
