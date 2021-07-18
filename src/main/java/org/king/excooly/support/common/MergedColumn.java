package org.king.excooly.support.common;

public class MergedColumn implements Merged {
	int startAt, numColMergeds;
	
	public MergedColumn(int startAt, int numColMergeds) {
		this.startAt = startAt;
		this.numColMergeds = numColMergeds;
	}

	@Override
	public int startAt() {
		return startAt;
	}

	public int getNumColMergeds() {
		return numColMergeds;
	}
}
