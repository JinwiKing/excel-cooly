package org.king.excooly.support.common;

import java.util.Map;

import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 对象属性值序列化器。将对象属性序列化导Excel表格中。
 * @author king
 */
public class PropertyValueDynamicSerializer implements PropertyValueSerializer {
	private final Map<Class<?>, PropertyValueSerializer> serializers;
	
	public PropertyValueDynamicSerializer(Map<Class<?>, PropertyValueSerializer> serializers) {
		this.serializers = serializers;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Class<?> javaValueType = serializationParam.getJavaValue().getClass();
		PropertyValueSerializer serializer = serializers.get(javaValueType);
		return serializer.serialize(serializationParam);
	}
}
