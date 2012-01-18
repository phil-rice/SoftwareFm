package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.IForgottonPasswordProcessor;
import org.softwareFm.utilities.collections.Lists;

public class ForgottonPasswordProcessorMock implements IForgottonPasswordProcessor {

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
		emails.add(email);
		return errorMessage;
	}

}
