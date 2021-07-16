package org.king.excooly;

import org.king.excooly.support.poi.PoiExcelCooly;

public class ExcelCoolyFactory {

	public static ExcelCooly newInstance() {
		return new PoiExcelCooly();
	}

}
