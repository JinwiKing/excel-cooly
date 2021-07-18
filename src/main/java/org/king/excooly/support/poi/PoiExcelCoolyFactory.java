package org.king.excooly.support.poi;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.ExcelCooly;
import org.king.excooly.ExcelCoolyFactory;
import org.king.excooly.support.PropertyValueSerializer;

public class PoiExcelCoolyFactory implements ExcelCoolyFactory {
	private static final Map<Class<?>, ExcelCellValueDeserializer> DEFAULT_DESERIALIZERS = new HashMap<>(); 
	private static final Map<Class<?>, PropertyValueSerializer> DEFAULT_SERIALIZERS = new HashMap<>(); 
	
	static {
		DEFAULT_DESERIALIZERS.put(byte.class, new ByteCodec());
		DEFAULT_DESERIALIZERS.put(Byte.class, new ByteCodec());
		DEFAULT_DESERIALIZERS.put(short.class, new ShortCodec());
		DEFAULT_DESERIALIZERS.put(Short.class, new ShortCodec());
		DEFAULT_DESERIALIZERS.put(int.class, new IntegerCodec());
		DEFAULT_DESERIALIZERS.put(Integer.class, new IntegerCodec());
		DEFAULT_DESERIALIZERS.put(long.class, new LongCodec());
		DEFAULT_DESERIALIZERS.put(Long.class, new LongCodec());
		DEFAULT_DESERIALIZERS.put(float.class, new FloatCodec());
		DEFAULT_DESERIALIZERS.put(Float.class, new FloatCodec());
		DEFAULT_DESERIALIZERS.put(double.class, new DoubleCodec());
		DEFAULT_DESERIALIZERS.put(Double.class, new DoubleCodec());
		DEFAULT_DESERIALIZERS.put(String.class, new StringCodec());
		DEFAULT_DESERIALIZERS.put(Date.class, new DateCodec());
		DEFAULT_DESERIALIZERS.put(BigDecimal.class, new BigDecimalCodec());
		
		DEFAULT_DESERIALIZERS.put(Collection.class, new ListCodec());
		DEFAULT_DESERIALIZERS.put(List.class, new ListCodec());
		DEFAULT_DESERIALIZERS.put(ArrayCodec.class, new ArrayCodec());
		
		DEFAULT_SERIALIZERS.put(byte.class, new ByteCodec());
		DEFAULT_SERIALIZERS.put(Byte.class, new ByteCodec());
		DEFAULT_SERIALIZERS.put(short.class, new ShortCodec());
		DEFAULT_SERIALIZERS.put(Short.class, new ShortCodec());
		DEFAULT_SERIALIZERS.put(int.class, new IntegerCodec());
		DEFAULT_SERIALIZERS.put(Integer.class, new IntegerCodec());
		DEFAULT_SERIALIZERS.put(long.class, new LongCodec());
		DEFAULT_SERIALIZERS.put(Long.class, new LongCodec());
		DEFAULT_SERIALIZERS.put(float.class, new FloatCodec());
		DEFAULT_SERIALIZERS.put(Float.class, new FloatCodec());
		DEFAULT_SERIALIZERS.put(double.class, new DoubleCodec());
		DEFAULT_SERIALIZERS.put(Double.class, new DoubleCodec());
		DEFAULT_SERIALIZERS.put(String.class, new StringCodec());
		DEFAULT_SERIALIZERS.put(Date.class, new DateCodec());
		DEFAULT_SERIALIZERS.put(BigDecimal.class, new BigDecimalCodec());
	}

	@Override
	public ExcelCooly newInstance() {
		return new PoiExcelCooly(new HashMap<>(DEFAULT_DESERIALIZERS), new HashMap<>(DEFAULT_SERIALIZERS));
	}
}
