package org.king.excooly.support.common;

public class MergedRow implements Merged {
	int startAt, numRowMergeds;
	MergedList<MergedColumn> mergedColumns = new MergedList<>();
	
	public MergedRow(int startAt, int numRowMergeds) {
		this.startAt = startAt;
		this.numRowMergeds = numRowMergeds;
	}
	
	@Override
	public int startAt() {
		return startAt;
	}

	public int getNumRowMergeds() {
		return numRowMergeds;
	}
	
	public void addMergedColumn(MergedColumn mergedColumn) {
		mergedColumns.add(mergedColumn);
	}
	
	public MergedColumn getMergedColumn(int startAt) {
		return mergedColumns.get(startAt);
	}
	
	public boolean containsColumn(int startAt) {
		return mergedColumns.get(startAt) != null;
	}
}
