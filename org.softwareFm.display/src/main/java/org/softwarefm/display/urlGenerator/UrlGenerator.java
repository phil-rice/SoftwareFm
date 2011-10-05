package org.softwareFm.display.urlGenerator;

import java.text.MessageFormat;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.utilities.strings.Strings;

public class UrlGenerator implements IUrlGenerator {

	private final String prefix;

	public UrlGenerator(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String findUrlFor(String entity, Object data) {
		if (data == null)
			return null;
		if (!(data instanceof String))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustBeAString, data, data.getClass().getName()));
		String cleanUrl = Strings.forUrl((String) data);
		if (cleanUrl.length() < 2)
			return "";
		return "/softwareFm/" + prefix + "s/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl;
	}

}
