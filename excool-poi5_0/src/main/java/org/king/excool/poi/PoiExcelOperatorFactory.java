package org.king.excool.poi;

import org.king.excool.ExcelCellValueDeserializer;
import org.king.excool.ExcelOperator;
import org.king.excool.ExcelOperatorFactory;
import org.king.excool.PropertyValueSerializer;
import org.king.excool.poi.resolver.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class PoiExcelOperatorFactory implements ExcelOperatorFactory {
	private static final Map<Class<?>, ExcelCellValueDeserializer> DEFAULT_DESERIALIZERS = new HashMap<>();
	private static final Map<Class<?>, PropertyValueSerializer> DEFAULT_SERIALIZERS = new HashMap<>();
	private static final SimpleCellValueReader SIMPLE_CELL_VALUE_READER;
	
	static {
		AbstractValueResolver byteResolver = new ByteResolver(), shortResolver = new ShortResolver(),
				integerResolver = new IntegerResolver(), longResolver = new LongResolver(),
				floatResolver = new FloatResolver(), doubleResolver = new DoubleResolver(),
				stringResolver = new StringResolver(), dateResolver = new DateResolver(),
				bigDecimalResolver = new BigDecimalResolver(), localDateResolver = new LocalDateResolver(),
				localTimeResolver = new LocalTimeResolver(), localDateTimeResolver = new LocalDateTimeResolver();
		SimpleCellValueReader simpleCellValueReader = new CellValueHelper();

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
	public ExcelOperator newInstance() {
		return new PoiExcelCooly(new HashMap<>(DEFAULT_DESERIALIZERS), new HashMap<>(DEFAULT_SERIALIZERS), SIMPLE_CELL_VALUE_READER);
	}
}
