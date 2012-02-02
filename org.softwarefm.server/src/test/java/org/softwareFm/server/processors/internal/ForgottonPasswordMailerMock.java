package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.server.processors.IForgottonPasswordMailer;

public class ForgottonPasswordMailerMock implements IForgottonPasswordMailer {

	public final List<String> emails = Lists.newList();
	private final String magicString;

	public ForgottonPasswordMailerMock(String magicString) {
		this.magicString = magicString;
	}

	@Override
	public String process(String email) {
		emails.add(email);
		return magicString;
	}

}