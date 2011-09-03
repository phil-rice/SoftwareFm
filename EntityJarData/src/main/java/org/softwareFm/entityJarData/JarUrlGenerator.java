package org.softwareFm.entityJarData;

import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;


public class JarUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityJar);
	}

}
