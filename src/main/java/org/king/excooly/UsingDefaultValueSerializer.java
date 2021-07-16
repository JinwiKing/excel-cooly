package org.king.excooly;

import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

public final class UsingDefaultValueSerializer implements PropertyValueSerializer {

	@Override
	public void serialize(PropertyValueSerializationParameter serializationParam) {
		throw new UnsupportedOperationException();
	}
}
