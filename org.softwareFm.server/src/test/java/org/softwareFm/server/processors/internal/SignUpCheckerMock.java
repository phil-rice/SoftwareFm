package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.collections.Lists;

public class SignUpCheckerMock implements ISignUpChecker {

	public final List<String> emails = Lists.newList();
	public final List<String> salts = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	private final String errorMessage;
	private final String crypto;

	public SignUpCheckerMock(String errorMessage, String crypto) {
		this.errorMessage = errorMessage;
		this.crypto = crypto;
	}

	@Override
	public SignUpResult signUp(String email, String salt, String passwordHash) {
		emails.add(email);
		salts.add(salt);
		passwordHashes.add(passwordHash);
		return new SignUpResult(errorMessage, crypto);
	}

}
