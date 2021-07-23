package org.king.excooly.support.poi;

import java.util.List;

class EvaluatedWritingExcelColumn {
	WritingExcelColumn column;
	Object data;
	int[][] rowRanges;	// each [start, end]
	List<EvaluatedWritingExcelColumn> cascadedColumns;
}
