package org.king.excooly.support.poi;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excooly.support.ExcelValueDeserializerParameter;

class BigDecimalResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		if (cell == null) return null;
		BigDecimal v = null;
		int ct = cell.getCellType();
		
		switch (ct) {
			case Cell.CELL_TYPE_STRING: v = new BigDecimal(cell.getStringCellValue()); break;
			case Cell.CELL_TYPE_NUMERIC: v = BigDecimal.valueOf(cell.getNumericCellValue()); break;
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to BigDecimal");
		}
		return v;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		BigDecimal obj = (BigDecimal) serializationParam.getJavaValue();
		if(obj == null) return null;
		return obj.toString();
	}
}
