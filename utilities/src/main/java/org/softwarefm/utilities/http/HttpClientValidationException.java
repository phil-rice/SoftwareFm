package org.softwarefm.utilities.http;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.softwarefm.utilities.constants.UtilityMessages;

public class HttpClientValidationException extends RuntimeException {

	public final List<String> errors;

	public HttpClientValidationException(List<String> errors) {
		super(MessageFormat.format(UtilityMessages.clientValidationException, errors));
		this.errors = Collections.unmodifiableList(errors);
	}
}
