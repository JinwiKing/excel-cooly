package org.king.excool;

/**
 * 对象属性值序列化器。将对象属性序列化导Excel表格中。
 * @author wangjw5
 */
@FunctionalInterface
public interface PropertyValueSerializer {

	/**
	 * 序列化
	 */
	String serialize(PropertyValueSerializationParameter serializationParam);
}
