package org.king.excooly.support.poi;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.king.excooly.ExcelRowValueWriter;
import org.king.excooly.support.common.ExcelUtils;

/**
 * 默认Excel行数据写入器
 * @author king
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
			serializationParam.javaValue = writingCell.valueGetter.get(writingParam.instance, writingCell);
			serializationParam.cell = ExcelUtils.getOrCreateCell(row, j);
			serializationParam.wb = writingParam.workbook;
			serializationParam.columnConfiguration = writingCell.configuration;
			writingCell.serializer.serialize(serializationParam);
		}
		return writingParam.writeToLineNo + 1;
	}

}
