package org.king.excooly.support.poi;

/**
 * 封装解析的注解和注解对象的属性或方法
 * @author wangjw5
 */
public class FocusingExcelColumn {
	ExcelColumnConfiguration configuration;
	
	public FocusingExcelColumn(ExcelColumnConfiguration columnConfiguration) {
		super();
		this.configuration = columnConfiguration;
	}

	public ExcelColumnConfiguration getConfiguration() {
		return configuration;
	}
}
