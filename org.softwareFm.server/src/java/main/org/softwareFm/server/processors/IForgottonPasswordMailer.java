package org.softwareFm.server.processors;

public interface IForgottonPasswordMailer {

	/** returns magic string. Throws exception if errors */
	String process(String email);

}
