package org.king.excool;

public abstract class AbstractDefaultExcelWriterConfiguration implements ExcelWriterConfiguration {

	@Override
	public int getTitleRowIndex(String sheetName) {
		return 0;
	}

	@Override
	public Object processObjectValue(String sheetName, String fieldName, Object srcValue) {
		return srcValue;
	}

	@Override
	public int getSheetArrangementWeight(String sheetName) {
		return 0;
	}

}
