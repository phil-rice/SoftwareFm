package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.AbstractUsageReader;

public class UsageReaderForServer extends AbstractUsageReader {

	protected final IGitOperations gitOperations;

	public UsageReaderForServer(IGitOperations gitOperations, IUserReader user, IUrlGenerator userUrlGenerator) {
		super(user, userUrlGenerator);
		this.gitOperations = gitOperations;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription) {
		Map<String, Map<String, List<Integer>>> projectDetails = (Map) gitOperations.getFile(projectFileDescription);
		return projectDetails;
	}

}
