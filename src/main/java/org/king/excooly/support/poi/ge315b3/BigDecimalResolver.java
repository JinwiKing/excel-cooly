package org.king.excooly.support.poi.ge315b3;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.AbstractValueResolver;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

public class BigDecimalResolver extends AbstractValueResolver {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		if (cell == null) return null;
		BigDecimal v = null;
		CellType ct = cell.getCellType();
		
		switch (ct) {
			case STRING: v = new BigDecimal(cell.getStringCellValue()); break;
			case NUMERIC: v = BigDecimal.valueOf(cell.getNumericCellValue()); break;
			case BLANK: v = null; break;
			case ERROR: v = null; break;
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
