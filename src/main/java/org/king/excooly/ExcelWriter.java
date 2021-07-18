package org.king.excooly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.poi.BigDecimalCodec;
import org.king.excooly.support.poi.ByteCodec;
import org.king.excooly.support.poi.DateCodec;
import org.king.excooly.support.poi.DefaultExcelRowValueWriter;
import org.king.excooly.support.poi.DoubleCodec;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.ExcelRowValueWritingParam;
import org.king.excooly.support.poi.FloatCodec;
import org.king.excooly.support.poi.IntegerCodec;
import org.king.excooly.support.poi.LongCodec;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;
import org.king.excooly.support.poi.ShortCodec;
import org.king.excooly.support.poi.StringCodec;
import org.king.excooly.support.poi.WritingExcelColumn;

import com.centaline.util.ReflectionUtils;

/**
 * Excel写入器 
 * @author wangjw5
 */
@ExcelTable
public class ExcelWriter {
	
	/**
	 * PropertyValueSerializer代表
	 * @author wangjw5
	 */
	private static class PropertyValueSerializerDelegate implements PropertyValueSerializer{
		/**
		 * 自定义的序列化器
		 */
		PropertyValueSerializer customValueSerializer;
		/**
		 * 默认的序列化器映射
		 */
		Map<Class<?>, PropertyValueSerializer> defaultSerializers;

		PropertyValueSerializerDelegate(Map<Class<?>, PropertyValueSerializer> defaultSerializers) {
			super();
			this.defaultSerializers = defaultSerializers;
		}

		@Override
		public void serialize(PropertyValueSerializationParameter serializationParam) {
			if(customValueSerializer != null) customValueSerializer.serialize(serializationParam);
			else {
				PropertyValueSerializer defaultSerializer = defaultSerializers.get(serializationParam.javaValue.getClass());
				defaultSerializer.serialize(serializationParam);
			}
		}
	}
	
	/**
	 * 用JavaBean的Getter方法获取属性值的获取器
	 */
	private static final JavaValueGetter PROPERTY_VALUE_JAVA_BEAN_METHOD_GETTER = new JavaValueGetter() {
		@Override
		public Object get(WritingExcelColumn writingExcelCell, Object instance) {
			try {
				return writingExcelCell.objctJavaBeanGetter.invoke(instance);
			} catch (Exception e) {
				throw new RuntimeException(instance.getClass().getName() + " 实例的属性 " + ((Field) writingExcelCell.annotatedObject).getName() + " 读取失败", e);
			}
		}
	};
	
	/**
	 * Java的属性值直接读取器。直接修改该类的accessible进行读取。
	 */
	private static JavaValueGetter PROPERTY_VALUE_REFLECTION_GETTER = new JavaValueGetter() {

		@Override
		public Object get(WritingExcelColumn writingExcelCell, Object instance) {
			Field field = (Field) writingExcelCell.annotatedObject;
			if(!field.isAccessible()) field.setAccessible(true);
			try {
				return field.get(instance);
			} catch (Exception e) {
				throw new RuntimeException(instance.getClass().getName() + " 实例的属性 " + field.getName() + " 读取失败", e);
			}
		}
	};
	
	/**
	 * Java方法返回值读取器
	 */
	private static JavaValueGetter METHOD_RETURN_VALUE_GETTER = new JavaValueGetter() {

		@Override
		public Object get(WritingExcelColumn writingExcelCell, Object instance) {
			try {
				return writingExcelCell.objctJavaBeanGetter.invoke(instance);
			} catch (Exception e) {
				throw new RuntimeException(instance.getClass().getName() + " 实例的方法 " + ((Method) writingExcelCell.annotatedObject).getName() + " 调用失败", e);
			}
		}
	};
	
	private static final Logger LOGGER = Logger.getLogger(ExcelWriter.class);
	private static final Map<Class<?>, PropertyValueSerializer> DEFAULT_SERIALIZERS;
	private static final Map<Class<?>, PropertyValueSerializer> CUSTOMER_SERIALIZERS = new ConcurrentHashMap<>();
	
	static {
		Map<Class<?>, PropertyValueSerializer> defaultSerializers = new HashMap<>();
		defaultSerializers.put(byte.class, new ByteCodec());
		defaultSerializers.put(Byte.class, new ByteCodec());
		defaultSerializers.put(short.class, new ShortCodec());
		defaultSerializers.put(Short.class, new ShortCodec());
		defaultSerializers.put(int.class, new IntegerCodec());
		defaultSerializers.put(Integer.class, new IntegerCodec());
		defaultSerializers.put(long.class, new LongCodec());
		defaultSerializers.put(Long.class, new LongCodec());
		defaultSerializers.put(float.class, new FloatCodec());
		defaultSerializers.put(Float.class, new FloatCodec());
		defaultSerializers.put(double.class, new DoubleCodec());
		defaultSerializers.put(Double.class, new DoubleCodec());
		defaultSerializers.put(String.class, new StringCodec());
		defaultSerializers.put(Date.class, new DateCodec());
		defaultSerializers.put(BigDecimal.class, new BigDecimalCodec());
		DEFAULT_SERIALIZERS = Collections.unmodifiableMap(defaultSerializers);
	}
	
	/**
	 * 将数据写到随机名字的sheet内。
	 * @param wb sheet对应的excel
	 * @param data 数据
	 */
	public static void writeTo(Workbook wb, List<?> data) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");

		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		Sheet sheet = wb.createSheet();
		Class<?> dataType = getDataTypeFromData(data);
		if(dataType != null) innerWriteTo(wb, sheet, data, dataType, true);
	}
	
	/**
	 * 将数据写到指定sheet名的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheetName sheet名字。若sheet不存在则自动创建
	 * @param data 数据
	 */
	public static void writeTo(Workbook wb, String sheetName, List<?> data) {
		Sheet sheet = wb.createSheet();
		writeTo(wb, sheet, data, true);
	}
	
	/**
	 * 将数据写到指定sheet名的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheetName sheet名字。若sheet不存在则自动创建
	 * @param data 数据
	 * @param override 是否覆盖原有的内容
	 */
	public static void writeTo(Workbook wb, String sheetName, List<?> data, boolean override) {
		Sheet sheet = wb.createSheet();
		writeTo(wb, sheet, data, override);
	}
	
	/**
	 * 将数据写到指定的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheet 指定写入的sheet
	 * @param data 数据
	 */
	public static void writeTo(Workbook wb, Sheet sheet, List<?> data) {
		writeTo(wb, sheet, data, true);
	}
	
	/**
	 * 将数据写到指定的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheet 指定写入的sheet
	 * @param data 数据
	 * @param override 是否覆盖原有的内容
	 */
	public static void writeTo(Workbook wb, Sheet sheet, List<?> data, boolean override) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");
		else if(sheet == null) throw new IllegalArgumentException("Sheet is null");
		
		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		Class<?> dataType = getDataTypeFromData(data);
		if(dataType != null) innerWriteTo(wb, sheet, data, dataType, override);
	}
	
	private static Class<?> getDataTypeFromData(List<?> data){
		Iterator<?> iterator = data.iterator();
		Object instance = null;
		while(instance == null && iterator.hasNext()) instance = iterator.next();
		return instance == null ? null : instance.getClass();
	}
	
	/**
	 * 将数据写到随机名字的sheet内。
	 * @param wb sheet对应的excel
	 * @param data 数据
	 * @param dataType 数据的类型
	 */
	public static <DataType> void writeTo(Workbook wb, List<DataType> data, Class<DataType> dataType) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");
		else if(dataType == null) throw new IllegalArgumentException("Type is null");

		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		innerWriteTo(wb, wb.createSheet(), data, dataType, true);
	}
	
	/**
	 * 将数据写到指定sheet名的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheetName sheet名字。若sheet不存在则自动创建
	 * @param data 数据
	 * @param dataType 数据的类型
	 */
	public static <DataType> void writeTo(Workbook wb, String sheetName, List<DataType> data, Class<DataType> dataType) {
		writeTo(wb, sheetName, data, dataType, true);
	}

	/**
	 * 将数据写到指定sheet名的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheetName sheet名字。若sheet不存在则自动创建
	 * @param data 数据
	 * @param dataType 数据的类型
	 * @param override 是否覆盖原有的内容
	 */
	public static <DataType> void writeTo(Workbook wb, String sheetName, List<DataType> data, Class<DataType> dataType, boolean override) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");
		else if(sheetName == null) throw new IllegalArgumentException("Sheet name is null");
		else if(dataType == null) throw new IllegalArgumentException("Type is null");
		
		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		Sheet sheet = ExcelUtils.getOrCreateSheet(wb, sheetName);
		innerWriteTo(wb, sheet, data, dataType, override);
	}
	
	/**
	 * 将数据写到指定的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheet 指定写入的sheet
	 * @param data 数据
	 * @param dataType 数据的类型
	 */
	public static <DataType> void writeTo(Workbook wb, Sheet sheet, List<DataType> data, Class<DataType> dataType) {
		writeTo(wb, sheet, data, dataType, true);
	}
	
	/**
	 * 将数据写到指定的sheet内。
	 * @param wb sheet对应的excel
	 * @param sheet 指定写入的sheet
	 * @param data 数据
	 * @param dataType 数据的类型
	 * @param override 是否覆盖原有的内容
	 */
	public static <DataType> void writeTo(Workbook wb, Sheet sheet, List<DataType> data, Class<DataType> dataType, boolean override) {
		if(wb == null) throw new IllegalArgumentException("Work book is null");
		else if(sheet == null) throw new IllegalArgumentException("Sheet is null");
		else if(dataType == null) throw new IllegalArgumentException("Type is null");
		
		data = Optional.ofNullable(data).orElse(Collections.emptyList());
		innerWriteTo(wb, sheet, data, dataType, override);
	}
	
	private static void innerWriteTo(Workbook wb, Sheet sheet, List<?> data, Class<?> dataType, boolean override) {
		int rowNo = 0;
		if(!override) rowNo = sheet.getLastRowNum() + 1;
		
		List<WritingExcelColumn> containedFields = new ArrayList<>();
		Class<?> lookingClass = dataType;
		while(lookingClass != Object.class) {
			
			// A ExcelDateCell annotation and a ExcelCell annotation can annotate on a 
			// property or a method of an instance, so we need to scan all properties 
			// and all method of the instance.
			
			// On property
			for(Field field : lookingClass.getDeclaredFields()) {
				ExcelColumnConfiguration concreteExcelCol = ExcelUtils.getColumnConfiguration(field);
				if (concreteExcelCol != null) {
					
					// 决定将要使用的属性值获取器
					Method javaBeanGetter = null;
					JavaValueGetter valueGetter = null;
					if(!concreteExcelCol.valueGetterType.equals(UsingDefaultValueGetter.class)) {
						try {
							valueGetter = concreteExcelCol.valueGetterType.newInstance();
						} catch (Exception e) {
							throw new RuntimeException("无法实例化属性获取器", e);
						}
					}else {
						// 优先使用JavaBean的getter获取属性值，如果没有getter方法，则直接使用反射获取器
						try {
							javaBeanGetter = ReflectionUtils.getGetterOfField(field, lookingClass);
						} catch (Exception e) {
							if(LOGGER.isDebugEnabled()) LOGGER.debug("无法获取JavaBean的Getter方法，使用反射读取属性值的方法读取属性 " + field.getName());
						}
						valueGetter = javaBeanGetter != null ? PROPERTY_VALUE_JAVA_BEAN_METHOD_GETTER : PROPERTY_VALUE_REFLECTION_GETTER;
					}
					
					PropertyValueSerializer serializer = resolveValueSerializer(field.getType(), concreteExcelCol);
					
					containedFields.add(new WritingExcelColumn(concreteExcelCol, field, javaBeanGetter, valueGetter, serializer));
				}
			}
			
			// On method
			for(Method method : lookingClass.getDeclaredMethods()) {
				ExcelColumnConfiguration concreteExcelCol = ExcelUtils.getColumnConfiguration(method);
				if (concreteExcelCol != null) {
					
					// Make sure that the method which will be call for getting value to 
					// serialize to excel table is a non-parameter method. 
					if(method.getReturnType().equals(Void.TYPE)) continue;	// May be a setter
					else if (method.getParameterCount() > 0) throw new RuntimeException("请确保将返回值序列化到Excel表内的方法为无参方法");
					
					if (!method.isAccessible()) method.setAccessible(true);
					
					PropertyValueSerializer serializer = resolveValueSerializer(method.getReturnType(), concreteExcelCol);
					
					containedFields.add(new WritingExcelColumn(concreteExcelCol, method, method, METHOD_RETURN_VALUE_GETTER, serializer));
				}
			}
			
			lookingClass = lookingClass.getSuperclass();
		}
		containedFields.sort((f1, f2) -> f1.order - f2.order);
		
		ExcelTable excelTable = ExcelUtils.getOrUseDefaultExcelTableAnnotation(dataType);
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
	
	/**
	 * 决定将要使用的PropertyValueSerializer
	 */
	private static PropertyValueSerializer resolveValueSerializer(Class<?> valueType, ExcelColumnConfiguration excelCell) {
		PropertyValueSerializer valueSerializer = null;
		
		// Determine whether use a delegate to serialize the property value in case that 
		// the property value getter that defined by others returns the type of the value 
		// which is different from the original type of the value.
		boolean useSerializerDelegate = !excelCell.valueGetterType.equals(UsingDefaultValueGetter.class);
		if(useSerializerDelegate) valueSerializer = new PropertyValueSerializerDelegate(DEFAULT_SERIALIZERS);
		if(!excelCell.serializerType.equals(UsingDefaultValueSerializer.class)) {
			PropertyValueSerializer customSerializer = CUSTOMER_SERIALIZERS.computeIfAbsent(excelCell.serializerType, clazz -> {
				try {
					return excelCell.serializer.newInstance();
				} catch (Exception e) {
					throw new RuntimeException("无法实例化属性序列化器", e);
				}
			});
			if(useSerializerDelegate) {
				((PropertyValueSerializerDelegate)valueSerializer).customValueSerializer = customSerializer;
				return valueSerializer;
			}
			valueSerializer = customSerializer;
		}else if(!useSerializerDelegate) valueSerializer = DEFAULT_SERIALIZERS.get(valueType);
		
		return valueSerializer;
	}
	
}
