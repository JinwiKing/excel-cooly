package org.king.excool.poi;

import org.junit.jupiter.api.Test;
import org.king.excool.ExcelOperator;
import org.king.excool.ExcelType;

import java.io.InputStream;
import java.util.List;

public class BasicTests {
	
	@Test
	public void testBasicRead() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean.class);
		System.out.println(ls.size());
		
		for(BasicTestBean bean : ls) System.out.println(bean);
	}
	
	@Test
	public void testBasicMultiRead() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean.class);
		System.out.println(ls.size());
		excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");
		ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean.class);
		System.out.println(ls.size());
		
		for(BasicTestBean bean : ls) System.out.println(bean);
	}
	
	@Test
	public void testBasicWrite() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel0.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean.class);

		factory.newInstance().write(ExcelType.XLSX, "e:/test/test.xlsx", "Sheet1", ls, BasicTestBean.class);
	}
	
	@Test
	public void testMerged() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel1_containedMerged.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean2> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean2.class);
		System.out.println(ls.size());
		
		for(BasicTestBean2 bean : ls) System.out.println(bean);
	}
	
	@Test
	public void testCascaded() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel2_cascaded.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean3> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean3.class);
		System.out.println(ls.size());
		
		for(BasicTestBean3 bean : ls) System.out.println(bean);
	}

	@Test
	public void testWriteCascaded() {
		InputStream excelIs = Thread.currentThread().getClass().getResourceAsStream("/testExcel2_cascaded.xlsx");

		PoiExcelOperatorFactory factory = new PoiExcelOperatorFactory();
		List<BasicTestBean3> ls = factory.newInstance().read(excelIs, "Sheet1", BasicTestBean3.class);
		System.out.println(ls.size());
		for(BasicTestBean3 bean : ls) System.out.println(bean);

		factory.newInstance().write(ExcelType.XLSX, "e:/test/test.xlsx", "Sheet1", ls, BasicTestBean3.class);
	}
}
