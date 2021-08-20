package org.king.excool.poi;

import org.king.excool.DefaultExcelValueDeserializerParameter;
import org.king.excool.ExcelCellValueDeserializer;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ListResolver implements CellValueResolver {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Object[] cells = deserializerParam.cells();
		int numCells = cells.length;
		Class<?> targetJavaType = deserializerParam.targetJavaType();
		Map<Class<?>, ExcelCellValueDeserializer> deserilizers = deserializerParam.deserializers();
		List ls = new ArrayList<>(numCells);
		
		for(int i = 0; i < numCells; i++) {
			Object cell = cells[i];
			DefaultExcelValueDeserializerParameter parameter = (DefaultExcelValueDeserializerParameter) deserializerParam;
			parameter.setCells(new Object[] {cell});
			ls.add(deserilizers.get(targetJavaType).deserialize(parameter));
		}
		return ls;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		throw new UnsupportedOperationException();
	}

}
