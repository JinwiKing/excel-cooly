package org.king.excooly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.poi.UsingDefaultValueDeserializer;
import org.king.excooly.support.poi.UsingDefaultValueSetter;

/**
 * Excel单元格
 * @author wangjw5
 */
@Inherited
@Target({
	ElementType.FIELD,
	ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCell {
	
	/**
	 * For deserializing, it is using to indicate whether the excel column must be existed. If
	 * true, deserializing process will be stop when column not found. Default false.
	 */
	boolean requiredForDeserializing() default false;
	
	/**
	 * For deserializing, it is a zero-based index corresponding to an excel column.
	 * 如果反序列化后的java对象是自定义对象时，
	 * <br>
	 * Priority: columnIndex > columnName > name > namePattern.
	 * <br>
	 * Using {@code columnIndex} when it great than or equal to 0. Using {@code columnName} 
	 * when {@code columnIndex} less than 0. Using {@code name} when {@code columnIndex} 
	 * less than 0 and the length of {@code columnName} is not positive. Using {@code matchPattern} 
	 * when {@code columnIndex} less than 0 and the length of {@code columnName} is not positive and
	 * the length of {@code name} is not positive.
	 */
	int columnIndex() default -1;
	
	/**
	 * For deserializing, it is a name corresponding to an excel column.
	 * <br>
	 * Priority: columnIndex > columnName > name > namePattern.
	 * <br>
	 * Using {@code columnIndex} when it great than or equal to 0. Using {@code columnName} 
	 * when {@code columnIndex} less than 0. Using {@code name} when {@code columnIndex} 
	 * less than 0 and the length of {@code columnName} is not positive. Using {@code matchPattern} 
	 * when {@code columnIndex} less than 0 and the length of {@code columnName} is not positive and
	 * the length of {@code name} is not positive.
	 */
	String columnName() default "";
	
	/**
	 * Alias for {@code columnName}
	 * <br>
	 * Priority: columnIndex > columnName > name > namePattern.
	 * <br>
	 * Using {@code columnIndex} when it great than or equal to 0. Using {@code columnName} 
	 * when {@code columnIndex} less than 0. Using {@code name} when {@code columnIndex} 
	 * less than 0 and the length of {@code columnName} is not positive. Using {@code matchPattern} 
	 * when {@code columnIndex} less than 0 and the length of {@code columnName} is not positive and
	 * the length of {@code name} is not positive.
	 */
	String name() default "";
	
	/**
	 * For deserializing, it is used for matching an excel column by column name.
	 * <br>
	 * Priority: columnIndex > columnName > name > namePattern.
	 * <br>
	 * Using {@code columnIndex} when it great than or equal to 0. Using {@code columnName} 
	 * when {@code columnIndex} less than 0. Using {@code name} when {@code columnIndex} 
	 * less than 0 and the length of {@code columnName} is not positive. Using {@code matchPattern} 
	 * when {@code columnIndex} less than 0 and the length of {@code columnName} is not positive and
	 * the length of {@code name} is not positive.
	 */
	String matchPattern() default "";
	
	/**
	 * For deserializing, it indicates which deserializer will be used in deserializing excel cell values
	 */
	Class<? extends ExcelCellValueDeserializer> deserializer() default UsingDefaultValueDeserializer.class;
	
	/**
	 * For deserializing, it indicates which deserializer will be used in setting deserialized excel cell 
	 * values to java property
	 * 注意：当该注解注解在方法上时，该配置将失效。
	 */
	Class<? extends JavaValueSetter> setter() default UsingDefaultValueSetter.class;
	
	/**
	 * For serializing,
	 */
	boolean requiredForSerializing() default true;
	
	/**
	 * 序列化到Excel表格时，属性对应表格顺序。值越大越靠后。
	 */
	int order() default 0;
	
	/**
	 * 指定读取属性的获取器，使用该获取器获取属性值。
	 * 注意：当该注解注解在方法上时，该配置将失效。
	 */
	Class<? extends JavaValueGetter> getter() default UsingDefaultValueGetter.class;
	
	/**
	 * 指定序列化器对属性值到excel单元格进行序列化
	 */
	Class<? extends PropertyValueSerializer> serializer() default UsingDefaultValueSerializer.class;

	/**
	 * 如果java类型是容器类型，需要提供具体类型
	 */
	Class<?> concreteType() default NoConcreteType.class;
}
