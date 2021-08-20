package org.king.excool;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Excel枚举列。目前仅支持String, byte, short, int, float, double
 * @author wangjw5
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface ExcelEnumCell {
	
	/**
	 * For deserializing, it is using to indicate whether the excel column must be existed. If
	 * true, deserializing process will be stop when column not found. Default false.
	 */
	boolean requiredForDeserializing() default false;
	
	/**
	 * For deserializing, it is a zero-based index corresponding to an excel column.
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
	Class<? extends ExcelCellValueDeserializer> deserializer() default ExcelCellValueDeserializer.class;
	
	/**
	 * For deserializing, it indicates which deserializer will be used in setting deserialized excel cell 
	 * values to java property
	 * 注意：当该注解注解在方法上时，该配置将失效。
	 */
	Class<? extends JavaPropertySetter> setter() default JavaPropertySetter.class;
	
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
	Class<? extends JavaPropertyGetter> getter() default UsingDefaultPropertyGetter.class;
	
	/**
	 * 指定序列化器对属性值到excel单元格进行序列化
	 */
	Class<? extends PropertyValueSerializer> serializer() default PropertyValueSerializer.class;
	
	/**
	 * 默认Java属性到Excel的值。如果是空串并且defaultExcelValAsEmptyString为false，则返回null。。
	 */
	String defaultExcelVal() default "";
	
	/**
	 * 默认Java属性到Excel的值是否设置为空串
	 */
	boolean defaultExcelValAsEmptyString() default false; 
	
	/**
	 * 默认Excel值到Java的属性值。如果是NULL，如果是空串并且defaultPropertyValAsEmptyString为false，则返回null。
	 */
	String defaultPropertyVal() default "";
	
	/**
	 * 默认Excel值到Java的属性值是否设置为空串。false时表示使用null
	 */
	boolean defaultPropertyValAsEmptyString() default false; 
	
	/**
	 * 枚举列表
	 */
	ExcelColumnEnum[] enums() default {};
}
