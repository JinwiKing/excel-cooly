package org.king.excool.poi.resolver;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.king.excool.ExcelColumnConfiguration;
import org.king.excool.ExcelValueDeserializerParameter;
import org.king.excool.PropertyValueSerializationParameter;
import org.king.excool.poi.CellValueResolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 负责将Excel值转为Date类型以及将Date类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
public class DateResolver implements CellValueResolver {
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMATER = new ThreadLocal<>();
	
	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if (cell == null) return null;
		String v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_STRING: v = cell.getStringCellValue(); break;
			case Cell.CELL_TYPE_FORMULA: {
				try {
					v = cell.getStringCellValue();
				} catch (Exception e) {
					v = cell.getCellFormula();
					byte ecc = cell.getErrorCellValue();
					if(FormulaError.isValidCode(ecc)) v = null;
				}
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: v = String.valueOf(cell.getNumericCellValue()); break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to date");
		}
		
		SimpleDateFormat sdf = DATE_FORMATER.get();
		if(sdf == null) {
			sdf = new SimpleDateFormat();
			DATE_FORMATER.set(sdf);
		}
		sdf.applyPattern(configuration.getDateFormat());
		Date date = null;
		try {
			date = sdf.parse(v);
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
