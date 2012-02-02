package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.server.processors.SignUpResult;

public class SignUpCheckerMock implements ISignUpChecker {

	public final List<String> emails = Lists.newList();
	public final List<String> salts = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	public final List<String> softwareFmIds = Lists.newList();
	private final String errorMessage;
	private final String crypto;

	public SignUpCheckerMock(String errorMessage, String crypto) {
		this.errorMessage = errorMessage;
		this.crypto = crypto;
	}

	@Override
	public SignUpResult signUp(String email, String salt, String passwordHash, String softwareFmId) {
		emails.add(email);
		salts.add(salt);
		passwordHashes.add(passwordHash);
		softwareFmIds.add(softwareFmId);
		return new SignUpResult(errorMessage, crypto);
	}

}