package org.king.excool.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.king.excool.ExcelCellValueDeserializer;
import org.king.excool.ExcelValueDeserializerParameter;

public class BasicTestBeanText2IntDeserializer implements ExcelCellValueDeserializer {

	@Override
	public Object doDeserialize(ExcelValueDeserializerParameter deserializerParam) {
		if(deserializerParam.cells()[0] instanceof Cell) {
			Cell cell = (Cell) deserializerParam.cells()[0];
			String str = cell.getStringCellValue();
			if(str.equals("文本1")) return 10000;
			
			return -1;
		}
		return -2;
	}

}
