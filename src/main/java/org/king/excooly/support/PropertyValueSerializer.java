package org.king.excooly.support;

import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 对象属性值序列化器。将对象属性序列化导Excel表格中。
 * @author wangjw5
 */
@FunctionalInterface
public interface PropertyValueSerializer {

	/**
	 * 序列化
	 * @param propertyValue 即将序列化的属性的值
	 * @param cell 目标单元格
	 * @param wb 单元格所在的excel
	 * @param excelCell 读取到的即将序列化的属性的注解
	 */
	void serialize(PropertyValueSerializationParameter serializationParam);
}
