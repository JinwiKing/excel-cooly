package org.king.excooly.support.logger;

public class Log4jLoggerAdapter implements Logger {
	private final org.apache.log4j.Logger logger;
	
	public Log4jLoggerAdapter(Class<?> clazz) {
		this.logger = org.apache.log4j.Logger.getLogger(clazz);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(Object message) {
		logger.error(message);
	}

	@Override
	public void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(Object message) {
		logger.info(message);
	}

	@Override
	public void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(Object message) {
		logger.debug(message);
	}

	@Override
	public void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(Object message) {
		logger.trace(message);
	}

	@Override
	public void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}

}
