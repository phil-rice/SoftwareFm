package org.softwareFm.server.processors;

public interface ILoginChecker {

	/** returns null if failed, crypto key if suceeded */
	String login(String email, String passwordHash);

}
