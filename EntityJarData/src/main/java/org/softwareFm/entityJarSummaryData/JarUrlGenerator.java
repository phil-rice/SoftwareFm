package org.softwareFm.entityJarSummaryData;

import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.constants.RepositoryConstants;

public class JarUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityJar);
	}

}