package org.king.excooly.support.logger;

public interface Logger {
	
	boolean isErrorEnabled();
	
	void error(Object message);
	
	void error(Object message, Throwable t);
	
	boolean isInfoEnabled();
	
	void info(Object message);
	
	void info(Object message, Throwable t);
	
	boolean isDebugEnabled();
	
	void debug(Object message);
	
	void debug(Object message, Throwable t);
	
	boolean isTraceEnabled();
	
	void trace(Object message);
	
	void trace(Object message, Throwable t);
}
