package dk.erst.delis.oxalis.sender.request;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyMethodInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String invocationDescription = getInvocationDescription(invocation);
		writeToLog(log, "Entering " + invocationDescription);
		try {
			Object rval = invocation.proceed();
			writeToLog(log, "Exiting " + invocationDescription);
			return rval;
		} catch (Throwable ex) {
			writeToLog(log, "Exception thrown in " + invocationDescription, ex);
			throw ex;
		}
	}

	private void writeToLog(Logger log, String message) {
		log.debug(message);
	}

	private void writeToLog(Logger log, String message, Throwable ex) {
		log.debug(message, ex);
	}

	protected String getInvocationDescription(MethodInvocation invocation) {
		String className = invocation.getThis().getClass().getName();
		return "method '" + invocation.getMethod().getName() + "' of class [" + className + "]";
	}
}