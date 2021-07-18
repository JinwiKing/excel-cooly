package org.king.excooly.support.common;

import org.king.excooly.ExcelCellValueDeserializer;
import org.king.excooly.support.ExcelValueDeserializerParameter;

public abstract class AbstractExcelValueDeserializer implements ExcelCellValueDeserializer {

	@Override
	public Object deserialize(ExcelValueDeserializerParameter deserializerParam) {
		try {
			return innerDeserialize(deserializerParam);
		} catch (Exception e) {
			throw new RuntimeException("Deserializing row no. " + (deserializerParam.rowIdx() + 1) + ", column no. " + (deserializerParam.colIdx() + 1) + " failure" + e.getMessage());
		}
	}

	protected abstract Object innerDeserialize(ExcelValueDeserializerParameter deserializerParam);
}
