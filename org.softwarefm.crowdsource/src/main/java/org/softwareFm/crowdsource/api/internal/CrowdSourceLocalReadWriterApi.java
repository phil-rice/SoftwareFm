package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.git.IGitOperations;

public class CrowdSourceLocalReadWriterApi extends AbstractCrowdSourceReadWriterApi{

	private final IGitOperations gitOperations;

	public CrowdSourceLocalReadWriterApi(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}
	
	@Override
	public IGitOperations gitOperations() {
		return gitOperations;
	}

}
