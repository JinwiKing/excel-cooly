package org.king.excool;

import java.util.HashMap;
import java.util.Map;

public class MergedAddressMap {
	private Map<String, MergedAddress> map = new HashMap<>();
	
	public void put(int startRow, int startCol, MergedAddress address) {
		String key = startRow + "," + startCol;
		map.put(key, address);
	}
	
	public MergedAddress get(int startRow, int startCol) {
		String key = startRow + "," + startCol;
		return map.get(key);
	}
}
