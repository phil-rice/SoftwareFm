package org.softwareFm.common;

import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public class LocalGroupsReader extends AbstractGroupReader {

	protected final IGitLocal gitLocal;

	public LocalGroupsReader(IUrlGenerator urlGenerator, IGitLocal gitLocal) {
		super(urlGenerator);
		this.gitLocal = gitLocal;
	}

	@Override
	protected String findUrl(String groupId) {
		String url = urlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}

	@Override
	protected String getFileAsString(IFileDescription groupFileDescription) {
		return gitLocal.getFileAsString(groupFileDescription);
	}

	@Override
	public void refresh(String groupId) {
		gitLocal.clearCache(findUrl(groupId));
	}
}
