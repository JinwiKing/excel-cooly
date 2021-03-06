package org.king.excooly.support.poi;

import java.lang.reflect.Array;
import java.util.Map;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.support.ExcelValueDeserializerParameter;

class ArrayResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Object[] cells = deserializerParam.cells();
		int numCells = cells.length;
		Class<?> targetJavaType = deserializerParam.targetJavaType();
		Map<Class<?>, ExcelCellValueDeserializer> deserilizers = deserializerParam.deserializers();
		Object array = Array.newInstance(targetJavaType, numCells);
		
		for(int i = 0; i < numCells; i++) {
			Object cell = cells[i];
			PoiExcelValueDeserializerParameter parameter = (PoiExcelValueDeserializerParameter) deserializerParam;
			parameter.cells = new Object[] {cell};
			Array.set(array, i, deserilizers.get(targetJavaType).deserialize(parameter));
		}
		return array;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		throw new UnsupportedOperationException();
	}
}
