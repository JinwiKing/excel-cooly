package org.king.excooly.support;

import org.king.excooly.support.poi.ExcelColumnConfiguration;

public interface ExcelValueDeserializerParameter {
	
	/**
	 * A zero-base index for indicating current deserializing row 
	 */
	int rowIdx();

	/**
	 * A zero-base index for indicating current deserializing column 
	 */
	int colIdx();
	
	/**
	 * Current deserializing column name 
	 */
	String colName();

	/**
	 * Current deserializing column configuration 
	 */
	ExcelColumnConfiguration configuration();

	/**
	 * Current deserializing excel cell. Provided by third-part tools
	 */
	Object cell();
}
