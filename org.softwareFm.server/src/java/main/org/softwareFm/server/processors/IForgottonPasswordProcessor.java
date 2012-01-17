package org.softwareFm.server.processors;

public interface IForgottonPasswordProcessor {

	/** returns error message, null if no error */
	String process(String email);

}
