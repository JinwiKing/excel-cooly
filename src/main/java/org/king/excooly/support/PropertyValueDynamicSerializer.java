package org.king.excooly.support;

import java.util.Map;

import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 对象属性值序列化器。将对象属性序列化导Excel表格中。
 * @author wangjw5
 */
public class PropertyValueDynamicSerializer implements PropertyValueSerializer {
	private final Map<Class<?>, PropertyValueSerializer> serializers;
	
	public PropertyValueDynamicSerializer(Map<Class<?>, PropertyValueSerializer> serializers) {
		this.serializers = serializers;
	}

	public void serialize(PropertyValueSerializationParameter serializationParam) {
		Class<?> javaValueType = serializationParam.getJavaValue().getClass();
		PropertyValueSerializer serializer = serializers.get(javaValueType);
		serializer.serialize(serializationParam);
	}
}
