package org.king.excooly;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BasicTests {
	
	@Test
	public void testBasicRead() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");
		
		List<BasicTestBean> ls = ExcelWorker.read(excelIs, "Sheet1", BasicTestBean.class);
		System.out.println(ls.size());
		
		for(BasicTestBean bean : ls) System.out.println(bean);
	}
	
	@Test
	public void testBasicWrite() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");
		
		List<BasicTestBean> ls = ExcelWorker.read(excelIs, "Sheet1", BasicTestBean.class);
		
		ExcelWorker.write("e:/test/test.xlsx", "Sheet1", ls);
	}
	
	@Test
	public void testMerged() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel1_containedMerged.xlsx");
		
		List<BasicTestBean2> ls = ExcelWorker.read(excelIs, "Sheet1", BasicTestBean2.class);
		System.out.println(ls.size());
		
		for(BasicTestBean2 bean : ls) System.out.println(bean);
	}
	
	@Test
	public void testEmbedded() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel2_embedded.xlsx");
		
		List<BasicTestBean3> ls = ExcelWorker.read(excelIs, "Sheet1", BasicTestBean3.class);
		System.out.println(ls.size());
		
		for(BasicTestBean3 bean : ls) System.out.println(bean);
	}
}
