package org.softwareFm.eclipse.project;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(Map<String, Object> userDetailMap, String month) {
		IFileDescription projectFileDescription = IProject.Utils.makeProjectFileDescription(userUrlGenerator, userDetailMap, month, userCryptoKey);
		Map<String, Map<String, List<Integer>>> data = (Map) gitLocal.getFile(projectFileDescription);
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
