package org.king.excooly.support.common;

import org.king.excooly.support.ExcelColumnMatcher;

public class ExcelColumnFullNameMatcher implements ExcelColumnMatcher {
	private final String columnName;

	public ExcelColumnFullNameMatcher(String columnName) {
		if(columnName == null) throw new IllegalArgumentException("Column name is null");
		this.columnName = columnName;
	}

	@Override
	public boolean isMatchWith(String columnName) {
		return this.columnName.equals(columnName);
	}
}
