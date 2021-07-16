package org.king.excooly;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.king.excooly.support.JavaValueSetter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.JavaValueGetter;
import org.king.excooly.support.poi.UsingDefaultValueDeserializer;
import org.king.excooly.support.poi.UsingDefaultValueSetter;

/**
 * Excel枚举列。目前仅支持String, byte, short, int, float, double
 * @author wangjw5
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface ExcelEnumColumn {
	/**
	 * 序列化到Excel表格时，属性对应表格顺序。值越大越靠后。
	 */
	int order() default 0;
	
	/**
	 * 序列化到Excel表格时，该属性对应的表格宽度。
	 */
	int width() default 8;
	
	/**
	 * 对应Excel表格的列名，也叫表头名.
	 */
	String name() default "";
	
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
	 * 对应Excel表格的列名的匹配表达式，用于Excel值反序列化到Java属性值。
	 * 反序列化时name和namePattern的优先级：
	 * 当 namePattern.trim.length > 0 时，使用namePattern，否则
	 * 当 name.trim.length > 0 时，使用name，否则
	 * 使用属性名作为待处理列名
	 */
	String namePattern() default "";
	
	/**
	 * 指定Excel值到Java值反序列化器
	 */
	Class<? extends ExcelCellValueDeserializer> deserializer() default UsingDefaultValueDeserializer.class;
	
	/**
	 * 指定属性注入器，使用该注入器注入属性值
	 * 注意：当该注解注解在方法上时，该配置将失效。
	 */
	Class<? extends JavaValueSetter> setter() default UsingDefaultValueSetter.class;
	
	/**
	 * 反序列化时，是否必须存在当前表格对应的列
	 */
	boolean required() default false;
	
	/**
	 * 是否在序列化时（Java -> Excel）忽略。
	 */
	boolean ignoreSerialization() default false;
	
	/**
	 * 是否在反序列化时(Excel -> Java)忽略。当此项为true时，将会忽略required选项。
	 */
	boolean ignoreDeserialization() default false;
	
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
