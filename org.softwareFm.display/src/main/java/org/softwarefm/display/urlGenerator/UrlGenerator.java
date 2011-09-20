package org.softwareFm.display.urlGenerator;

import java.text.MessageFormat;

import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.strings.UrlRipperResult;
import org.softwareFm.utilities.strings.Urls;

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
		UrlRipperResult ripperResult = Urls.rip((String) data);
		String cleanUrl = Strings.onlyKeep(ripperResult.resourcePath.toLowerCase(), "abcdefghijklmnopqrstuvwxyz0123456789._");
		if (cleanUrl.length() < 2)
			return "";
		return "/" + prefix + "s/" + Math.abs(cleanUrl.hashCode()) % 1000 + "/" + cleanUrl;
	}
}
