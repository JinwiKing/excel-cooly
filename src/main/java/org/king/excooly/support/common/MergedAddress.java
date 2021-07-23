package org.king.excooly.support.common;

public class MergedAddress {
	private int startRow;
	private int endRow;
	private int startColum;
	private int endColumn;
	
	public MergedAddress(int startRow, int endRow, int startColum, int endColumn) {
		super();
		this.startRow = startRow;
		this.endRow = endRow;
		this.startColum = startColum;
		this.endColumn = endColumn;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getStartColum() {
		return startColum;
	}

	public void setStartColum(int startColum) {
		this.startColum = startColum;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}
}
