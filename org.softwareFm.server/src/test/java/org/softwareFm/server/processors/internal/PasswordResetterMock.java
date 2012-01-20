package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.utilities.collections.Lists;

public class PasswordResetterMock implements IPasswordResetter {

	private final String password;
	public final List<String> magicStrings = Lists.newList();

	public PasswordResetterMock(String password) {
		this.password = password;
	}

	@Override
	public String reset(String magicString) {
		magicStrings.add(magicString);
		return password;
	}

}