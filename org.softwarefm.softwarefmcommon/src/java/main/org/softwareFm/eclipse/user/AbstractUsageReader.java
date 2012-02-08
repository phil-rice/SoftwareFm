package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

abstract public class AbstractUsageReader implements IUsageReader {

	protected final IUserReader user;
	protected final IUrlGenerator userUrlGenerator;

	public AbstractUsageReader(IUserReader user, IUrlGenerator userUrlGenerator) {
		this.user = user;
		this.userUrlGenerator = userUrlGenerator;
	}

	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String projectCryptoKey, String month) {
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);

		Map<String, Map<String, List<Integer>>> projectDetails = getProjectDetails(projectFileDescription);
		return projectDetails;
	}

	abstract protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription);



}
