package org.king.excooly.support.common;

import java.util.regex.Pattern;

import org.king.excooly.support.ExcelColumnMatcher;

public class ExcelColumnPatternMatcher implements ExcelColumnMatcher {
	private final Pattern pattern;

	public ExcelColumnPatternMatcher(String pattern) {
		if(pattern == null) throw new IllegalArgumentException("Pattern is null");
		this.pattern = Pattern.compile(pattern);
	}

	public ExcelColumnPatternMatcher(Pattern pattern) {
		if(pattern == null) throw new IllegalArgumentException("Pattern is null");
		this.pattern = pattern;
	}

	@Override
	public boolean isMatchWith(String columnName) {
		return pattern.matcher(columnName).matches();
	}
}
