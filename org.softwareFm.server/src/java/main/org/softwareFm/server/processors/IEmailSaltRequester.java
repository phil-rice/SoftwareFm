package org.softwareFm.server.processors;

public interface IEmailSaltRequester {

	String getSalt(String email);
}
