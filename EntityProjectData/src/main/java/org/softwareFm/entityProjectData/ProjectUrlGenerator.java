package org.softwareFm.entityProjectData;

import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.constants.RepositoryConstants;


public class ProjectUrlGenerator implements IUrlGenerator {

	@Override
	public String apply(String from) {
		return IUrlGenerator.Utils.makeUrl(from, RepositoryConstants.entityProject);
	}

}
