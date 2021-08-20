package org.king.excool.poi;

import org.king.excool.ExcelCellValueDeserializer;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.DefaultExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;

import java.lang.reflect.Array;
import java.util.Map;

class ArrayResolver implements CellValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Object[] cells = deserializerParam.cells();
		int numCells = cells.length;
		Class<?> targetJavaType = deserializerParam.targetJavaType();
		Map<Class<?>, ExcelCellValueDeserializer> deserilizers = deserializerParam.deserializers();
		Object array = Array.newInstance(targetJavaType, numCells);
		
		for(int i = 0; i < numCells; i++) {
			Object cell = cells[i];
			DefaultExcelValueDeserializerParameter parameter = (DefaultExcelValueDeserializerParameter) deserializerParam;
			parameter.setCells(new Object[] {cell});
			Array.set(array, i, deserilizers.get(targetJavaType).deserialize(parameter));
		}
		return array;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		throw new UnsupportedOperationException();
	}
}
