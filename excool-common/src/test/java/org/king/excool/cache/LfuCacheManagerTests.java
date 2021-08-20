package org.king.excool.cache;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LfuCacheManagerTests {
	Logger logger = Logger.getLogger(getClass()); 

	@Test
	public void basicTest1() {
		LfuCacheManager cacheManager = new LfuCacheManager(1);
		
		Assertions.assertNull(cacheManager.get("no key"));
		
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		Assertions.assertNull(cacheManager.get("key"));
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
	}

	@Test
	public void basicTest2() {
		LfuCacheManager cacheManager = new LfuCacheManager(2);
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
	}

	@Test
	public void basicTest3() {
		LfuCacheManager cacheManager = new LfuCacheManager(2);
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		cacheManager.add("key3", 3);
		Assertions.assertNull(cacheManager.get("key2"));
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key4", 4);
		Assertions.assertNull(cacheManager.get("key3"));
		Assertions.assertNull(cacheManager.get("key2"));
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
	}

	@Test
	public void basicTest4() {
		
		// in same frequency, drops very old one
		// no.1
		
		LfuCacheManager cacheManager = new LfuCacheManager(2);
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		cacheManager.add("key3", 3);
		Assertions.assertNull(cacheManager.get("key"));	// old one
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
	}

	@Test
	public void basicTest5() {
		
		// in same frequency, drops very old one
		// no.2
		
		LfuCacheManager cacheManager = new LfuCacheManager(3);
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		cacheManager.add("key3", 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		cacheManager.add("key3", 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		
		cacheManager.add("key4", 4);
		Assertions.assertNull(cacheManager.get("key"));	// old one
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		val = cacheManager.get("key4");
		Assertions.assertEquals(val, 4);
	}

	@Test
	public void basicTest6() {
		
		// in same frequency, drops very old one
		// no.3
		
		LfuCacheManager cacheManager = new LfuCacheManager(3);
		cacheManager.add("key", 1);
		Object val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		cacheManager.add("key", 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		cacheManager.add("key2", 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key2");
		Assertions.assertEquals(val, 2);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		
		cacheManager.add("key3", 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		cacheManager.add("key3", 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		
		cacheManager.add("key4", 4);
		Assertions.assertNull(cacheManager.get("key2"));	// old one
		val = cacheManager.get("key");
		Assertions.assertEquals(val, 1);
		val = cacheManager.get("key3");
		Assertions.assertEquals(val, 3);
		val = cacheManager.get("key4");
		Assertions.assertEquals(val, 4);
	}

	@Test
	public void basicTest7() {
		LfuCacheManager cacheManager = new LfuCacheManager(1);
		
		Object val1 = new Object(), val2 = new Object();
		
		cacheManager.add("key", val1);
		Object val = cacheManager.get("key");
		Assertions.assertSame(val, val1);
		cacheManager.add("key", val2);
		val = cacheManager.get("key");
		Assertions.assertSame(val, val2);
	}

	@Test
	public void basicTest8() {
		LfuCacheManager cacheManager = new LfuCacheManager(1);
		
		Object val1 = new Object(), val2 = new Object();
		
		cacheManager.add("key", val1);
		Object val = cacheManager.get("key");
		Assertions.assertSame(val, val1);
		cacheManager.add("key", val1);
		val = cacheManager.get("key");
		Assertions.assertSame(val, val1);
		
		cacheManager.add("key2", val2);
		val = cacheManager.get("key2");
		Assertions.assertSame(val, val2);
		
		val = cacheManager.get("key");
		Assertions.assertNull(val);;
	}
}
