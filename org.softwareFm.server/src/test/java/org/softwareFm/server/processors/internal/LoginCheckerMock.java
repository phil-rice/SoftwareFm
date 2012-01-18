package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.ILoginChecker;
import org.softwareFm.utilities.collections.Lists;

public class LoginCheckerMock implements ILoginChecker {
	public final List<String> emails = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	private String crypto;

	public LoginCheckerMock(String crypto) {
		this.crypto = crypto;
	}

	@Override
	public String login(String email, String passwordHash) {
		emails.add(email);
		passwordHashes.add(passwordHash);
		return crypto;
	}

	public void setCrypto(String crypto) {
		this.crypto = crypto;
	}
}
