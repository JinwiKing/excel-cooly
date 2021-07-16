package org.king.excooly;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.king.excooly.support.poi.ExcelRowValueWritingParam;
import org.king.excooly.support.poi.PropertyValueSerializationParameter;
import org.king.excooly.support.poi.WritingExcelColumn;

/**
 * 默认Excel行数据写入器
 * @author wangjw5
 */
public class DefaultExcelRowValueWriter implements ExcelRowValueWriter {
	
	public static final class Singleton {
		public static final DefaultExcelRowValueWriter INSTANCE = new DefaultExcelRowValueWriter();
	}

	@Override
	public int write(ExcelRowValueWritingParam writingParam) {
		Row row = ExcelUtils.getOrCreateRow(writingParam.sheet, writingParam.writeToLineNo);
		row.setHeight(writingParam.excelTable.dataRowHeight());
		PropertyValueSerializationParameter serializationParam = writingParam.serializationParam;
		List<WritingExcelColumn> writingExcelCells = writingParam.writingExcelColumns;
		int numFieldsNeedToWrite = writingExcelCells.size();
		for(int j = 0; j < numFieldsNeedToWrite; j++) {
			WritingExcelColumn writingCell = writingExcelCells.get(j);
			serializationParam.javaValue = writingCell.valueGetter.get(writingCell, writingParam.instance);
			serializationParam.cell = ExcelUtils.getOrCreateCell(row, j);
			serializationParam.wb = writingParam.workbook;
			serializationParam.columnConfiguration = writingCell.columnConfiguration;
			writingCell.serializer.serialize(serializationParam);
		}
		return writingParam.writeToLineNo + 1;
	}

}
