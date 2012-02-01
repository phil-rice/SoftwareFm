package org.softwareFm.server.processors.internal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.user.IProject;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.url.IUrlGenerator;

public class ProjectForLocal implements IProject {

	private final IGitLocal gitLocal;
	private final IUrlGenerator userUrlGenerator;
	private final String userCryptoKey;
	private final IHttpClient client;
	private final String softwareFmId;

	public ProjectForLocal(IGitLocal gitLocal, IUrlGenerator userUrlGenerator, IHttpClient client, String softwareFmId, String userCryptoKey) {
		this.gitLocal = gitLocal;
		this.userUrlGenerator = userUrlGenerator;
		this.client = client;
		this.softwareFmId = softwareFmId;
		this.userCryptoKey = userCryptoKey;
	}

	@Override
	public Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month) {
		IFileDescription projectFileDescription = IProject.Utils.makeProjectFileDescription(userUrlGenerator, userDetailMap, month, userCryptoKey);
		Map<String, Object> data = gitLocal.getFile(projectFileDescription);
		return data;
	}

	@Override
	public void addProjectDetails(Map<String, Object> userDetailMap, String groupId, String artifactId, String month, long day) {
		try {
			client.post(SoftwareFmConstants.usagePrefix).addParam(LoginConstants.softwareFmIdKey, softwareFmId).execute(IResponseCallback.Utils.noCallback()).get(CommonConstants.staleCachePeriodForTest, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
