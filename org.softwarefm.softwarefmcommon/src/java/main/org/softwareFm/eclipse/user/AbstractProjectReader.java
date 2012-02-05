package org.softwareFm.eclipse.user;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

abstract public class AbstractProjectReader implements IProjectReader {

	private final IUserReader user;
	private final IUrlGenerator userUrlGenerator;

	public AbstractProjectReader(IUserReader user, IUrlGenerator userUrlGenerator) {
		this.user = user;
		this.userUrlGenerator = userUrlGenerator;
	}

	protected IFileDescription getFileDescriptionForProject(String userCryptoKey, Map<String, Object> userDetailMap, String month) {
		String projectCryptoKey = user.getUserProperty(userDetailMap, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null)
			return null;// there is nothing to display for this user
		String userUrl = userUrlGenerator.findUrlFor(userDetailMap);
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}
