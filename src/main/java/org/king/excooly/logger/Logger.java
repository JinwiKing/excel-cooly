package org.king.excooly.logger;

public interface Logger {
	
	boolean isErrorEnabled();
	
	void error(Object o);
	
	void error(Object o, Throwable t);
	
	boolean isInfoEnabled();
	
	void info(Object o);
	
	void info(Object o, Throwable t);
	
	boolean isDebugEnabled();
	
	void debug(Object o);
	
	void debug(Object o, Throwable t);
	
	boolean isTraceEnabled();
	
	void trace(Object o);
	
	void trace(Object o, Throwable t);
}
