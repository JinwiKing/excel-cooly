package org.king.excooly.support.poi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.support.ExcelValueDeserializerParameter;
import org.king.excooly.support.PropertyValueSerializer;
import org.king.excooly.support.common.AbstractExcelValueDeserializer;

/**
 * 负责将Excel值转为Date类型以及将Date类型值转为Excel值的默认转换器。
 * @author wangjw5
 */
class DateCodec extends AbstractExcelValueDeserializer implements PropertyValueSerializer {

	@Override
	public Object innerDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		Cell cell = (Cell) deserializerParam.cells()[0];
		ExcelColumnConfiguration configuration = deserializerParam.configuration();
		if (cell == null) return null;
		String v = null;
		int ct = cell.getCellType();
		switch (ct) {
			case Cell.CELL_TYPE_BLANK: v = null; break;
			case Cell.CELL_TYPE_BOOLEAN: v = String.valueOf(cell.getBooleanCellValue()); break;
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
			case Cell.CELL_TYPE_ERROR: v = null; break;
			default: throw new IllegalStateException("Unsupported format cell type " + ct + " to string");
		}
		
		SimpleDateFormat format = new SimpleDateFormat(configuration.dateFormat);
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
	public void serialize(PropertyValueSerializationParameter serializationParam) {
		if(serializationParam.javaValue == null) return;
		
		Date obj = (Date) serializationParam.javaValue;
		serializationParam.cell.setCellValue(obj);
		
		String dateFormat = serializationParam.columnConfiguration.dateFormat;
		dateFormat = dateFormat == null ? "yyyy/MM/dd HH:mm:ss" : dateFormat;
		String finalDateFormat = dateFormat;
		CellStyle dateCellStyle = serializationParam.nameCellStyleMap.computeIfAbsent("__DEFAULT_SERIALIZER__DATE_CELL_STYLE__" + dateFormat, k -> {
			Workbook wb = serializationParam.wb;
			CellStyle dcs = wb.createCellStyle();
			dcs.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(finalDateFormat));
			return dcs;
		});
		serializationParam.cell.setCellStyle(dateCellStyle);
	}
}
