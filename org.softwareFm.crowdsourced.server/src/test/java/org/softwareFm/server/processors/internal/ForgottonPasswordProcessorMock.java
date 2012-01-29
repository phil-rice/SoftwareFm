package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.IForgottonPasswordMailer;
import org.softwareFm.utilities.collections.Lists;

public class ForgottonPasswordProcessorMock implements IForgottonPasswordMailer {

	public final List<String> emails = Lists.newList();

	private String errorMessage;

	public ForgottonPasswordProcessorMock(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String process(String email) {
		if (errorMessage != null)
			throw new RuntimeException(errorMessage);
		emails.add(email);
		return "someMagicString";
	}

}
