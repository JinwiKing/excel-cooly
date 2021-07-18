package org.king.excooly.support.common;

import java.util.ArrayList;
import java.util.List;

public class MergedList <Type extends Merged> {
	private final List<Type> arr = new ArrayList<>();
	
	public void add(Type merged) {
		if(merged == null) return;
		
		int size;
		if((size = arr.size()) == 0) {
			arr.add(merged);
			return;
		}
		
		int headStartAt = arr.get(0).startAt(), lastStartAt = arr.get(size - 1).startAt(), startAt = merged.startAt();
		if(startAt <= headStartAt) {
			if(startAt == headStartAt) arr.set(0, merged);
			else arr.add(0, merged);
			return;
		}else if(startAt >= lastStartAt) {
			if(startAt == lastStartAt) arr.set(size - 1, merged);
			else arr.add(merged);
			return;
		}
		
		int l = 0, r = size, m;
		while(l < r) {
			m = l + (r - l) / 2;
			int mStartAt = arr.get(m).startAt();
			if(mStartAt >= startAt) r = m;
			else l = m + 1;
		}
		if(l >= size) arr.add(merged);
		else if(arr.get(l).startAt() == startAt) arr.set(l, merged);
		else arr.add(l, merged);
	}
	
	public Type get(int startAt) {
		int size;
		if((size = arr.size()) == 0) return null;
		
		int l = 0, r = size, m;
		while(l < r) {
			m = l + (r - l) / 2;
			int mStartAt = arr.get(m).startAt();
			if(mStartAt >= startAt) r = m;
			else l = m + 1;
		}
		if(l < size && arr.get(l).startAt() == startAt) return arr.get(l);
		
		return null;
	}
	
	public void remove(int startAt) {
		int size;
		if((size = arr.size()) == 0) return;
		
		
		int l = 0, r = size, m;
		while(l < r) {
			m = l + (r - l) / 2;
			int mStartAt = arr.get(m).startAt();
			if(mStartAt >= startAt) r = m;
			else l = m + 1;
		}
		if(l < size && arr.get(l).startAt() == startAt) arr.remove(l);
	}
}
