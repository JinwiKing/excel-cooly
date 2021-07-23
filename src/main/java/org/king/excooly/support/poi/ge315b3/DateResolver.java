package org.king.excooly.support.poi.ge315b3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.poi.AbstractValueResolver;
import org.king.excooly.support.poi.ExcelColumnConfiguration;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;

/**
 * 负责将Excel值转为Date类型以及将Date类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class DateResolver extends AbstractValueResolver {
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMATER = new ThreadLocal<>();
	
	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if (cell == null) return null;
		String v = null;
		CellType ct = cell.getCellType();
		switch (ct) {
			case BLANK: v = null; break;
			case BOOLEAN: v = String.valueOf(cell.getBooleanCellValue()); break;
			case STRING: v = cell.getStringCellValue(); break;
			case FORMULA: {
				try {
					v = cell.getStringCellValue();
				} catch (Exception e) {
					v = cell.getCellFormula();
					byte ecc = cell.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case NUMERIC: v = String.valueOf(cell.getNumericCellValue()); break;
			case ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to string");
		}
		
		SimpleDateFormat format = new SimpleDateFormat(configuration.getDateFormat());
		Date date = null;
		try {
			date = format.parse(v);
		} catch (ParseException e) {
			// don't care
		}
		if(date == null && v != null && v.trim().length() > 0) date = DateUtil.getJavaDate(Double.parseDouble(v));
		return date;
	}

	@Override
	public String serialize(PropertyValueSerializationParameter serializationParam) {
		Date obj = (Date) serializationParam.getJavaValue();
		if(obj == null) return null;
		String format = serializationParam.getColumnConfiguration().getDateFormat();
		format = format == null ? "yyyy/MM/dd HH:mm:ss" : format;
		SimpleDateFormat sdf = DATE_FORMATER.get();
		if(sdf == null) {
			sdf = new SimpleDateFormat();
			DATE_FORMATER.set(sdf);
		}
		sdf.applyPattern(format);
		return sdf.format(obj);
	}
}
