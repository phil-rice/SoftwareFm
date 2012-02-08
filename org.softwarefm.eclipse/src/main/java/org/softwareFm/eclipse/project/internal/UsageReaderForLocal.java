package org.softwareFm.eclipse.project.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.AbstractUsageReader;

public class UsageReaderForLocal extends AbstractUsageReader {

	private final IGitLocal gitLocal;
	private final String userCryptoKey;

	public UsageReaderForLocal(IUserReader user, IUrlGenerator userUrlGenerator, IGitLocal gitLocal, String userCryptoKey) {
		super(user, userUrlGenerator);
		this.gitLocal = gitLocal;
		this.userCryptoKey = userCryptoKey;
	}

	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFm, String projectCryptoKey, String month) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(userCryptoKey, softwareFm, month);
		return getProjectDetails(projectFileDescription);
	}

	@Override
	protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription) {
		if (projectFileDescription != null) {
			Map<String, Map<String, List<Integer>>> data = (Map) gitLocal.getFile(projectFileDescription);
			if (data != null)
				return data;
		}
		return Collections.emptyMap();
	}

	protected IFileDescription getFileDescriptionForProject(String userCryptoKey, String userId, String month) {
		String projectCryptoKey = user.getUserProperty(userId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null)
			return null;// there is nothing to display for this user
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}
