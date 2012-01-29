package org.softwareFm.server.processors;

public interface IPasswordResetter {

	/** returns new password */
	String reset(String magicString);

	
}
