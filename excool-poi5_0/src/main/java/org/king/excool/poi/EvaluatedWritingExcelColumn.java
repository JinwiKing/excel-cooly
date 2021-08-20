package org.king.excool.poi;

import org.king.excool.WritingExcelColumn;

import java.util.List;

class EvaluatedWritingExcelColumn {
	WritingExcelColumn column;
	Object data;
	int[][] rowRanges;	// each [start, end]
	List<EvaluatedWritingExcelColumn> cascadedColumns;
}
