package org.softwareFm.entityProjectData;

import org.softwareFm.arc4eclipseRepository.api.IUrlGenerator;
import org.softwareFm.arc4eclipseRepository.constants.RepositoryConstants;


public class ProjectUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityProject);
	}

}
