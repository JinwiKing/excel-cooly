package org.king.excooly.support.logger;

public class StandardLogger implements Logger {

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(Object message) {
		System.err.println(message);
	}

	@Override
	public void error(Object message, Throwable t) {
		System.err.println(message);
		if(t != null) t.printStackTrace(System.err);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(Object message) {
		System.out.println(message);
	}

	@Override
	public void info(Object message, Throwable t) {
		System.out.println(message);
		if(t != null) t.printStackTrace(System.out);
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(Object message) {
		System.out.println(message);
	}

	@Override
	public void debug(Object message, Throwable t) {
		System.out.println(message);
		if(t != null) t.printStackTrace(System.out);
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public void trace(Object message) {
		System.out.println(message);
	}

	@Override
	public void trace(Object message, Throwable t) {
		System.out.println(message);
		if(t != null) t.printStackTrace(System.out);
	}

}
