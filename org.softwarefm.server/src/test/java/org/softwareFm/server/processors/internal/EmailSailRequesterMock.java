package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.IEmailSaltRequester;

public class EmailSailRequesterMock implements IEmailSaltRequester {

	private final List<String> emails = Lists.newList();
	private final String salt;

	public EmailSailRequesterMock(String salt) {
		this.salt = salt;
	}

	@Override
	public String getSalt(String email) {
		emails.add(email);
		return salt;
	}

}
