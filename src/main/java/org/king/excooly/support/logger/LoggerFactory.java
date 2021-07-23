package org.king.excooly.support.logger;

public class LoggerFactory {
	
	public static Logger getLogger(Class<?> clazz) {
		try {
			Class.forName("org.apache.log4j.Logger");
			return new Log4jLoggerAdapter(clazz);
		} catch (Exception e) {
			// don't care
		}
		return null;
	}
}
