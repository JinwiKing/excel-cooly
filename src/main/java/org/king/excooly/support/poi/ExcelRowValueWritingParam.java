package org.king.excooly.support.poi;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.king.excooly.ExcelTable;

/**
 * Excel行数据写入器参数
 * @author wangjw5
 */
public class ExcelRowValueWritingParam {
	/**
	 * ExcelTable注解
	 */
	ExcelTable excelTable;
	/**
	 * Excel
	 */
	Workbook workbook;
	/**
	 * Excel表
	 */
	Sheet sheet;
	/**
	 * 指定的行
	 */
	int writeToLineNo;
	/**
	 * 需要写的数据的对象实例
	 */
	Object instance;
	/**
	 * 需要写入的单元格所在的列
	 */
	List<WritingExcelColumn> writingExcelColumns;
	
	/**
	 * 内部使用的序列化参数
	 */
	PropertyValueSerializationParameter serializationParam = new PropertyValueSerializationParameter();
	
	public ExcelTable getExcelTable() {
		return excelTable;
	}
	
	public Workbook getWorkbook() {
		return workbook;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public int getWriteToLineNo() {
		return writeToLineNo;
	}
	
	public Object getInstance() {
		return instance;
	}

	public List<WritingExcelColumn> getWritingExcelColumns() {
		return writingExcelColumns;
	}
}
