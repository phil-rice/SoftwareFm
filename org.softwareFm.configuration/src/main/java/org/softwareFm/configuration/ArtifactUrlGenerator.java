package org.softwareFm.configuration;

import java.text.MessageFormat;

import org.softwareFm.display.urlGenerator.UrlGenerator;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.jdtBinding.api.RippedResult;

public class ArtifactUrlGenerator extends UrlGenerator {

	public ArtifactUrlGenerator() {
		super("artifact");
	}

	@Override
	public String findUrlFor(String entity, Object data) {
		if (data == null)
			return null;
		if (!(data instanceof RippedResult))
			throw new IllegalArgumentException(MessageFormat.format(ConfigurationConstants.expectedRippedResult, data, data.getClass()));
		RippedResult result = (RippedResult) data;
		return super.findUrlFor(entity, result.get(JdtConstants.hexDigestKey));
	}

}
