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
import org.king.excool.cache.LfuCacheManager;

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
				ReadingExcelColumnCollection colCollection = colCollectionQue.remove();
				for(ReadingExcelColumn col : colCollection.getColumns()) {
					ExcelColumnConfiguration colCfg = col.getColumnConfiguration();
					
					if(!colCfg.isCascaded()) {
						ExcelColumnMatcher usingMatcher = getExcelColumnMatcher(colCfg);
						matcherExcelColumnMap.put(usingMatcher, col);
						if(colCfg.isRequiredForDeserializing()) requiredCols.add(col);
					}else colCollectionQue.add(col.getCascadeCollection());
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
							DefaultReadingExcelColumn col = (DefaultReadingExcelColumn) matcherExcelColumnMap.get(matcher);
							if(matchedColumns.contains(col)) throw new RuntimeException("将列名" + colName + "匹配到已完成匹配的列 " + col.getColName() + " 中");

							col.setColIdx(colNum);
							col.setColName(colName);
							matchedColumns.add(col);
							requiredCols.remove(col);
						}
					}
				}
				if(requiredCols.size() > 0) {
					StringBuilder requiredColsNameBuilder = new StringBuilder();
					for(ReadingExcelColumn col : requiredCols) requiredColsNameBuilder.append(col.getColumnConfiguration().getName()).append(", ");
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
		collection.setType(dataClass);
		List<DefaultExcelColumnConfiguration> allColCfgs = collection.getConfigurations();
		
		while(lookingClass != Object.class) {
			// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
			// property or a method of an instance, so we need to scan all properties 
			// and all method of the instance.
			
			// On field
			for(Field field : lookingClass.getDeclaredFields()) {
				DefaultExcelColumnConfiguration colCfg = readFrom(field);
				if(colCfg == null) continue;
				colCfg.setBelongCollection(collection);
				
				if(colCfg.isIdCell) collection.setIdColumnConfiguration(colCfg);
				allColCfgs.add(colCfg);

				// 获取反序列化器
				Class<?> fieldType = field.getType();
				ExcelCellValueDeserializer deserializer = null;
				boolean isCascaded;
				if(isCascaded = colCfg.isCascaded()) {
					Class<?> cascadeType = colCfg.getConcreteType();
					if(!JDK_VALUE_TYPE.contains(cascadeType)) {
						DefaultExcelColumnConfigurationCollection columnConfigurationCollection = extractCfgFromClass(cascadeType, cascadeType);
						colCfg.setCascadeConfigurationCollection(columnConfigurationCollection);
						collection.getCascadedConfigurationCollections().add(columnConfigurationCollection);
						deserializer = CascadeDeserializer.INSTANCE;
					}
				}else if(!colCfg.getDeserializerType().equals(ExcelCellValueDeserializer.class)) {
					try {
						deserializer = colCfg.getDeserializerType().newInstance();
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性设置器", e);
					}
				}else {
					if(colCfg.isArray() || colCfg.isContainer()) {
						if(colCfg.isArray()) deserializer = deserializers.get(ArrayResolver.class);
						else deserializer = deserializers.get(fieldType);
						
						if(!JDK_VALUE_TYPE.contains(colCfg.getConcreteType())) deserializer = null;
					}else deserializer = deserializers.get(fieldType);
					
					if(deserializer == null) {
						throw new RuntimeException("No suitable deserializer for deserializing excel column value to field named " + field.getName());
					}
				}
				colCfg.setDeserializer(deserializer);

				// 级联对象也要提供setter
				
				// 决定java值注入器。如果指定了注入器，则使用指定的注入器，否则，如果指定了反序列化器，则认为该反序列化的返回值符合对应属性的类型；因为默认的反序列化器已经反序列化到指定
				// 的类型，所以直接查找对应类型的setter或直接注入即可
				Method javaBeanSetter = ReflectionUtils.getAccessibleSetterOfField(lookingClass, field, fieldType);
				if(!isCascaded && !colCfg.getPropertyGetterType().equals(JavaPropertyGetter.class)) {
					try {
						colCfg.setPropertySetter(colCfg.getPropertySetterType().newInstance());
					} catch (Exception e) {
						throw new RuntimeException("无法实例化属性注入器", e);
					}
				}else {
					if(javaBeanSetter == null) {
						field.setAccessible(true);
						colCfg.setPropertySetter(new JavaPropertyPropertyInjector(field));
					}else colCfg.setPropertySetter(new JavaBeanMethorSetter(javaBeanSetter));
				}
				
				// 级联对象也要提供getter
				
				JavaPropertyGetter valueGetter = null;
				Class<? extends JavaPropertyGetter> valueGetterType;
				if(!isCascaded && !(valueGetterType = colCfg.getPropertyGetterType()).equals(UsingDefaultPropertyGetter.class)) {
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
				colCfg.setPropertyGetter(valueGetter);
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
					colCfg.setDeserializer(deserializer);

					JavaPropertyGetter valueGetter = null;
					Class<? extends JavaPropertyGetter> valueGetterType;
					if(!(valueGetterType = colCfg.getPropertyGetterType()).equals(UsingDefaultPropertyGetter.class)) {
						try {
							valueGetter = valueGetterType.newInstance();
						} catch (Exception e) {
							throw new RuntimeException("无法实例化属性值读取器", e);
						}
					}else valueGetter = new JavaBeanMethodGetter(method);
					colCfg.setPropertyGetter(valueGetter);
				}
			}
			
			lookingClass = lookingClass.getSuperclass();
		}
		
		return collection;
	}
	
	private DefaultReadingExcelColumnCollection prepareReadingExcelColumn(ExcelColumnConfigurationCollection configurationCollection) {
		DefaultReadingExcelColumnCollection collection = new DefaultReadingExcelColumnCollection();
		collection.setConfigurationCollection(configurationCollection);
		List<ReadingExcelColumn> columns = collection.getColumns();
		for(DefaultExcelColumnConfiguration colCfg : configurationCollection.getConfigurations()) {
			DefaultReadingExcelColumn exCol = new DefaultReadingExcelColumn(colCfg);
			if(colCfg.isCascaded()) {
				exCol.setCascaded(true);
				DefaultReadingExcelColumnCollection columnCollection = prepareReadingExcelColumn(colCfg.getCascadeConfigurationCollection());
				exCol.setCascadeCollection(columnCollection);
				collection.getCascadedCollections().add(columnCollection);
			}
			columns.add(exCol);
		}
		return collection; 
	}
	
	private WritingExcelColumnCollection prepareWritingExcelColumn(ExcelColumnConfigurationCollection configurationCollection) {
		DefaultWritingExcelColumnCollection collection = new DefaultWritingExcelColumnCollection();
		collection.setConfigurationCollection(configurationCollection);
		List<WritingExcelColumn> columns = collection.getColumns();
		for(DefaultExcelColumnConfiguration colCfg : configurationCollection.getConfigurations()) {
			DefaultWritingExcelColumn exCol = new DefaultWritingExcelColumn(colCfg);
			if(colCfg.isCascaded()) {
				exCol.setCascaded(true);
				WritingExcelColumnCollection columnCollection = prepareWritingExcelColumn(colCfg.getCascadeConfigurationCollection());
				exCol.setCascadeCollection(columnCollection);
				collection.getCascadedCollections().add(columnCollection);
			}
			columns.add(exCol);
		}
		return collection; 
	}
	
	private static ExcelColumnMatcher getExcelColumnMatcher(ExcelColumnConfiguration configuration) {
		ExcelColumnMatcher matcher;
		String determiningStr = null;
		if((determiningStr = configuration.getMatchPattern()) != null && determiningStr.trim().length() > 0) {
			matcher = new ExcelColumnPatternMatcher(determiningStr);
		}else {
			if((determiningStr = configuration.getName()) == null || determiningStr.trim().length() <= 0) {
				if(configuration.getAccessibleObject() instanceof Field) {
					determiningStr = ((Field)configuration.getAccessibleObject()).getName();
				}else {
					determiningStr = ((Method) configuration.getAccessibleObject()).getName();
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
	private List extractData(MergedAddressMap mergedAddressMap, ReadingExcelColumnCollection colCollection, Class<?> dataType, Sheet sheet, int startRowNum, int endRowNum, int titleNum) {
		ReadingExcelColumn idColumn = colCollection.getIdColumn();
		DefaultExcelValueDeserializerParameter deserializerParam = new DefaultExcelValueDeserializerParameter();
		List data = new ArrayList();
		for(int rowNum = startRowNum, lastRowNum = endRowNum; rowNum >= 0 && rowNum <= lastRowNum; rowNum++) {
			if(rowNum == titleNum) continue;
			
			Row row = sheet.getRow(rowNum);
			if(row == null) continue;
			
			List<ReadingExcelColumn> columns = colCollection.getColumns();
			
			// If there specified an id column then use it to confirm how many rows data write to the data type, otherwise 
			// choose certain column as id column
			// [start, end]
			int start = rowNum, end = rowNum, idColNum;
			if(idColumn == null) {
				ReadingExcelColumn aCol = null;
				for(ReadingExcelColumn col : columns) {
					if(!col.isCascaded()) {
						aCol = col;
						break;
					}
				}
				if(aCol != null) idColNum = aCol.getColIdx();
				else idColNum = row.getFirstCellNum();
			}else idColNum = idColumn.getColIdx();
			MergedAddress mAddr = mergedAddressMap.get(rowNum, idColNum);
			if(mAddr != null) end = mAddr.getEndRow();
			
			Object instance;
			try {
				instance = dataType.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("无法实例化", e);
			}
			
			boolean addInstanceToList = false;
			for(ReadingExcelColumn col : colCollection.getColumns()) {
				
				if(col.isCascaded()) {
					ExcelColumnConfiguration colCfg = col.getColumnConfiguration();
					List ls = extractData(mergedAddressMap, col.getCascadeCollection(), colCfg.getConcreteType(), sheet, start, end, titleNum);
					if(colCfg.isArray()) {
						Object arr = Array.newInstance(dataType, ls.size());
						for(int i = 0, num = ls.size(); i < num; i++) Array.set(arr, i, ls.get(i));
						col.getColumnConfiguration().getPropertySetter().set(instance, arr, null);
					}else if(colCfg.isContainer()) {
						col.getColumnConfiguration().getPropertySetter().set(instance, ls, null);
					}else {
						int num;
						if((num = ls.size()) <= 0) col.getColumnConfiguration().getPropertySetter().set(instance, null, null);
						else if(num <= 1) col.getColumnConfiguration().getPropertySetter().set(instance, ls.get(0), null);
						else throw new RuntimeException("无法将多行数据转为单行数据");
					}
					
					addInstanceToList = true;
				}else {
					int colIdx;
					Cell cell = row.getCell(colIdx = col.getColIdx());
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

					deserializerParam.setRowIdx(rowNum);
					deserializerParam.setCells(cells);
					deserializerParam.setReadingColumn(col);
					deserializerParam.setDeserializes(deserializers);
					deserializerParam.setTargetJavaType(col.getColumnConfiguration().getConcreteType());
					Object value = col.getColumnConfiguration().getDeserializer().deserialize(deserializerParam);
					col.getColumnConfiguration().getPropertySetter().set(instance, value, col);
					
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
				WritingExcelColumnCollection rootColCollection = prepareWritingExcelColumn(rootCfgCollection);
				Queue<WritingExcelColumnCollection> colCollectionQue = new LinkedList<>();
				colCollectionQue.add(rootColCollection);
				List<WritingExcelColumn> containedFields = new ArrayList<>();
				while(!colCollectionQue.isEmpty()) {
					WritingExcelColumnCollection colCollection = colCollectionQue.remove();
					for(WritingExcelColumn col : colCollection.getColumns()) {
						ExcelColumnConfiguration colCfg = col.getColumnConfiguration();
						if(!colCfg.isRequiredForSerializing()) continue;
						
						if(!col.isCascaded()) containedFields.add(col); 
						else colCollectionQue.add(col.getCascadeCollection());
					}
				}
				containedFields.sort(Comparator.comparingInt(WritingExcelColumn::getOrder));
				for(int i = 0, numCols = containedFields.size(); i < numCols; i++) {
					((DefaultWritingExcelColumn)containedFields.get(i)).setOrder(i);
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
					WritingExcelColumn writingExcelCell = containedFields.get(i);
					Cell cell = ExcelUtils.getOrCreateCell(row, i);
//					cell.setCellStyle(titleStyle);
					cell.setCellValue(writingExcelCell.getColumName());
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
	private int writeRowData(int startRowNum, Object dataInstance, Sheet sheet, WritingExcelColumnCollection writingColumnCollection) {
		if(dataInstance == null) return startRowNum;
		
		List<int[]> rowRanges = new LinkedList<>();
		Set<Integer> writedSingleRowCols = new HashSet<>();
		Row row = ExcelUtils.getOrCreateRow(sheet, startRowNum);
		int numRowsUsing = 0;
		List<WritingExcelColumn> columns = writingColumnCollection.getColumns();
		for(WritingExcelColumn column : columns) {
			ExcelColumnConfiguration colCfg = column.getColumnConfiguration();
			Object prop = colCfg.getPropertyGetter().get(dataInstance, column);
			if(prop == null) continue;
			numRowsUsing = Math.max(numRowsUsing, 1);
			
			if(column.isCascaded()) {
				WritingExcelColumnCollection cascaded = column.getCascadeCollection();
				if(colCfg.isArray()) {
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
				}else if(colCfg.isContainer()) {
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
				parameter.setColumnConfiguration(colCfg);
				if(colCfg.isArray()) {
					Class<?> concreteType = colCfg.getConcreteType();
					int lenArr = Array.getLength(prop), tmpCountRow = 0;
					for(int i = 0; i < lenArr; i++) {
						Object dataInArr = Array.get(prop, i);
						if(dataInArr == null) continue;
						parameter.setJavaValue(dataInArr);
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
				}else if(colCfg.isContainer()) {
					Class<?> concreteType = colCfg.getConcreteType();
					Collection c = (Collection) prop;
					int i = 0, tmpCountRow = 0;
					for(Object dataInC : c) {
						if(dataInC == null) continue;
						parameter.setJavaValue(dataInC);
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
					parameter.setJavaValue(prop);
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

			DefaultExcelColumnConfigurationBuilder builder = new DefaultExcelColumnConfigurationBuilder();
			builder.requiredForDeserializing(cell.requiredForDeserializing())
					.columnIndex(cell.columnIndex())
					.columnName(cell.columnName())
					.name(cell.name())
					.matchPattern(cell.matchPattern())
					.deserializerType(cell.deserializer())
					.valueSetterType(cell.setter())
					.requiredForSerializing(cell.requiredForSerializing())
					.order(cell.order())
					.valueGetterType(cell.getter())
					.isDateCell(true)
					.dateFormat(cell.dateFormat())
			;
			cfg = builder.build();
		}else if((anno = ao.getAnnotation(ExcelEnumCell.class)) != null) {
			ExcelEnumCell cell = (ExcelEnumCell) anno;

			DefaultExcelColumnConfigurationBuilder builder = new DefaultExcelColumnConfigurationBuilder();
			builder.requiredForDeserializing(cell.requiredForDeserializing())
					.columnIndex(cell.columnIndex())
					.columnName(cell.columnName())
					.name(cell.name())
					.matchPattern(cell.matchPattern())
					.deserializerType(cell.deserializer())
					.valueSetterType(cell.setter())
					.requiredForSerializing(cell.requiredForSerializing())
					.order(cell.order())
					.valueGetterType(cell.getter())
					.isEnum(true)
			;
			ExcelColumnEnum[] enums = cell.enums();
			Map<String, String> propertyExcelMap = new HashMap<>(), excelPropertyMap = new HashMap<>();
			for(ExcelColumnEnum oneEnum : enums) {
				String java = oneEnum.javaVal(), excel = oneEnum.excelVal();
				propertyExcelMap.put(java, excel);
				excelPropertyMap.put(excel, java);
			}
			builder.propertyExcelMap(propertyExcelMap).excelPropertyMap(excelPropertyMap);
			String defaultVal = cell.defaultPropertyVal();
			defaultVal = defaultVal.equals("") && !cell.defaultPropertyValAsEmptyString() ? null : defaultVal;
			builder.defaultPropertyVal(defaultVal);
			defaultVal = cell.defaultExcelVal();
			defaultVal = defaultVal.equals("") && !cell.defaultExcelValAsEmptyString() ? null : defaultVal;
			builder.defaultExcelVal(defaultVal);
			cfg = builder.build();
		}else if((anno = ao.getAnnotation(ExcelCell.class)) != null) {
			ExcelCell cell = (ExcelCell) anno;

			DefaultExcelColumnConfigurationBuilder builder = new DefaultExcelColumnConfigurationBuilder();
			builder.requiredForDeserializing(cell.requiredForDeserializing())
					.columnIndex(cell.columnIndex())
					.columnName(cell.columnName())
					.name(cell.name())
					.matchPattern(cell.matchPattern())
					.deserializerType(cell.deserializer())
					.valueSetterType(cell.setter())
					.requiredForSerializing(cell.requiredForSerializing())
					.order(cell.order())
					.valueGetterType(cell.getter())
					.isEnum(true)
			;
			if(ao instanceof Field) {
				Field field = (Field) ao;
				builder.isContainer(Collection.class.isAssignableFrom(field.getType()));
				boolean isArray = field.getType().isArray();
				builder.isArray(isArray);
				if(isArray) builder.concreteType(field.getType().getComponentType());
			}
			cfg = builder.build();
		}else if((anno = ao.getAnnotation(ExcelCascadeCell.class)) != null) {
			ExcelCascadeCell cell = (ExcelCascadeCell) anno;

			DefaultExcelColumnConfigurationBuilder builder = new DefaultExcelColumnConfigurationBuilder();
			builder.isCascaded(true)
					.accessibleObject(ao)
					.concreteType(cell.concreteType())
					.requiredForSerializing(true)
			;
			if(ao instanceof Field) {
				Field field = (Field) ao;
				builder.isContainer(Collection.class.isAssignableFrom(field.getType()));
				boolean isArray = field.getType().isArray();
				builder.isArray(isArray);
				if(isArray) builder.concreteType(field.getType().getComponentType());
			}
			cfg = builder.build();
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
