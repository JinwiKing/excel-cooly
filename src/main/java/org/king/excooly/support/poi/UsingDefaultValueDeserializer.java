package org.king.excooly.support.poi;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.support.ExcelValueDeserializerParameter;

public class UsingDefaultValueDeserializer implements ExcelCellValueDeserializer{

	@Override
	public Object deserialize(ExcelValueDeserializerParameter deserializerParam) {
		throw new UnsupportedOperationException();
	}
}
