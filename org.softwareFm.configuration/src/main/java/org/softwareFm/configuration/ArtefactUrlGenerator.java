package org.softwareFm.configuration;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.jdtBinding.api.JdtConstants;

public class ArtefactUrlGenerator extends UrlGenerator {

	public ArtefactUrlGenerator() {
		super("artifact");
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findUrlFor(String entity, Object data) {
		if (data == null)
			return null;
		if (!(data instanceof Map))
			throw new IllegalArgumentException(MessageFormat.format(ConfigurationConstants.expectedRippedResult, data, data.getClass()));
		Map<Object,Object> result = (Map<Object, Object>) data;
		Object data1 = result.get(JdtConstants.hexDigestKey);
		if (data1 == null)
			return null;
		if (!(data1 instanceof String))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustBeAString, data1, data1.getClass().getName()));
		String digest = (String) data1;
		return "/softwareFm/artifacts/" + digest.substring(0, 3) + "/" + digest;
	}

}
