package org.king.excooly;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BasicTests {
	
	@Test
	public void testBasicFunction() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");
		
		List<BasicTestBean> ls = ExcelWorker.read(excelIs, BasicTestBean.class);
		
		for(BasicTestBean bean : ls) System.out.println(bean);
	}
}
