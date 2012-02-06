package org.softwareFm.eclipse.user;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
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

	protected IFileDescription getFileDescriptionForProject(String userCryptoKey, String userId, String month) {
		String projectCryptoKey = user.getUserProperty(userId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null)
			return null;// there is nothing to display for this user
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey,userId));
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}
