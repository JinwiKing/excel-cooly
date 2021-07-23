package org.king.excooly.support.poi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.ExcelCoolyFactory;
import org.king.excooly.ExcelOperation;
import org.king.excooly.support.PropertyValueSerializer;

public class PoiExcelCoolyFactory implements ExcelCoolyFactory {
	private static final Map<Class<?>, ExcelCellValueDeserializer> DEFAULT_DESERIALIZERS = new HashMap<>(); 
	private static final Map<Class<?>, PropertyValueSerializer> DEFAULT_SERIALIZERS = new HashMap<>(); 
	private static final SimpleCellValueReader SIMPLE_CELL_VALUE_READER;
	
	static {
		AbstractValueResolver byteResolver, shortResolver, integerResolver, longResolver ,floatResolver, 
			doubleResolver, stringResolver, dateResolver, bigDecimalResolver, localDateResolver,
			localTimeResolver, localDateTimeResolver;
		SimpleCellValueReader simpleCellValueReader;
		
		try {
			Class.forName("org.apache.poi.ss.usermodel.CellType");
			byteResolver = new org.king.excooly.support.poi.ge315b3.ByteResolver();
			shortResolver = new org.king.excooly.support.poi.ge315b3.ShortResolver();
			integerResolver = new org.king.excooly.support.poi.ge315b3.IntegerResolver();
			longResolver = new org.king.excooly.support.poi.ge315b3.LongResolver();
			floatResolver = new org.king.excooly.support.poi.ge315b3.FloatResolver();
			doubleResolver = new org.king.excooly.support.poi.ge315b3.DoubleResolver();
			stringResolver = new org.king.excooly.support.poi.ge315b3.StringResolver();
			dateResolver = new org.king.excooly.support.poi.ge315b3.DateResolver();
			bigDecimalResolver = new org.king.excooly.support.poi.ge315b3.BigDecimalResolver();
			localDateResolver = new org.king.excooly.support.poi.ge315b3.LocalDateResolver();
			localTimeResolver = new org.king.excooly.support.poi.ge315b3.LocalTimeResolver();
			localDateTimeResolver = new org.king.excooly.support.poi.ge315b3.LocalDateTimeResolver();
			
			simpleCellValueReader = new org.king.excooly.support.poi.ge315b3.CellValueHelper();
		} catch (Exception e) {
			byteResolver = new org.king.excooly.support.poi.l315b3.ByteResolver();
			shortResolver = new org.king.excooly.support.poi.l315b3.ShortResolver();
			integerResolver = new org.king.excooly.support.poi.l315b3.IntegerResolver();
			longResolver = new org.king.excooly.support.poi.l315b3.LongResolver();
			floatResolver = new org.king.excooly.support.poi.l315b3.FloatResolver();
			doubleResolver = new org.king.excooly.support.poi.l315b3.DoubleResolver();
			stringResolver = new org.king.excooly.support.poi.l315b3.StringResolver();
			dateResolver = new org.king.excooly.support.poi.l315b3.DateResolver();
			bigDecimalResolver = new org.king.excooly.support.poi.l315b3.BigDecimalResolver();
			localDateResolver = new org.king.excooly.support.poi.l315b3.LocalDateResolver();
			localTimeResolver = new org.king.excooly.support.poi.l315b3.LocalTimeResolver();
			localDateTimeResolver = new org.king.excooly.support.poi.l315b3.LocalDateTimeResolver();
			
			simpleCellValueReader = new org.king.excooly.support.poi.l315b3.CellValueHelper();
		}
		
		DEFAULT_DESERIALIZERS.put(byte.class, byteResolver);
		DEFAULT_DESERIALIZERS.put(Byte.class, byteResolver);
		DEFAULT_DESERIALIZERS.put(short.class, shortResolver);
		DEFAULT_DESERIALIZERS.put(Short.class, shortResolver);
		DEFAULT_DESERIALIZERS.put(int.class, integerResolver);
		DEFAULT_DESERIALIZERS.put(Integer.class, integerResolver);
		DEFAULT_DESERIALIZERS.put(long.class, longResolver);
		DEFAULT_DESERIALIZERS.put(Long.class, longResolver);
		DEFAULT_DESERIALIZERS.put(float.class, floatResolver);
		DEFAULT_DESERIALIZERS.put(Float.class, floatResolver);
		DEFAULT_DESERIALIZERS.put(double.class, doubleResolver);
		DEFAULT_DESERIALIZERS.put(Double.class, doubleResolver);
		DEFAULT_DESERIALIZERS.put(String.class, stringResolver);
		DEFAULT_DESERIALIZERS.put(Date.class, dateResolver);
		DEFAULT_DESERIALIZERS.put(BigDecimal.class, bigDecimalResolver);
		DEFAULT_DESERIALIZERS.put(LocalDate.class, localDateResolver);
		DEFAULT_DESERIALIZERS.put(LocalTime.class, localTimeResolver);
		DEFAULT_DESERIALIZERS.put(LocalDateTime.class, localDateTimeResolver);
		
		DEFAULT_DESERIALIZERS.put(Collection.class, new ListResolver());
		DEFAULT_DESERIALIZERS.put(List.class, new ListResolver());
		DEFAULT_DESERIALIZERS.put(ArrayResolver.class, new ArrayResolver());
		
		DEFAULT_SERIALIZERS.put(byte.class, byteResolver);
		DEFAULT_SERIALIZERS.put(Byte.class, byteResolver);
		DEFAULT_SERIALIZERS.put(short.class, shortResolver);
		DEFAULT_SERIALIZERS.put(Short.class, shortResolver);
		DEFAULT_SERIALIZERS.put(int.class, integerResolver);
		DEFAULT_SERIALIZERS.put(Integer.class, integerResolver);
		DEFAULT_SERIALIZERS.put(long.class, longResolver);
		DEFAULT_SERIALIZERS.put(Long.class, longResolver);
		DEFAULT_SERIALIZERS.put(float.class, floatResolver);
		DEFAULT_SERIALIZERS.put(Float.class, floatResolver);
		DEFAULT_SERIALIZERS.put(double.class, doubleResolver);
		DEFAULT_SERIALIZERS.put(Double.class, doubleResolver);
		DEFAULT_SERIALIZERS.put(String.class, stringResolver);
		DEFAULT_SERIALIZERS.put(Date.class, dateResolver);
		DEFAULT_SERIALIZERS.put(BigDecimal.class, bigDecimalResolver);
		DEFAULT_SERIALIZERS.put(LocalDate.class, localDateResolver);
		DEFAULT_SERIALIZERS.put(LocalTime.class, localTimeResolver);
		DEFAULT_SERIALIZERS.put(LocalDateTime.class, localDateTimeResolver);
		
		SIMPLE_CELL_VALUE_READER = simpleCellValueReader;
	}

	@Override
	public ExcelOperation newInstance() {
		return new PoiExcelCooly(new HashMap<>(DEFAULT_DESERIALIZERS), new HashMap<>(DEFAULT_SERIALIZERS), SIMPLE_CELL_VALUE_READER);
	}
}
