package org.softwareFm.eclipse.project.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.AbstractProjectReader;

public class ProjectForLocal extends AbstractProjectReader {

	private final IGitLocal gitLocal;
	private final String userCryptoKey;

	public ProjectForLocal(IUserReader user, IUrlGenerator userUrlGenerator, IGitLocal gitLocal, String userCryptoKey) {
		super(user, userUrlGenerator);
		this.gitLocal = gitLocal;
		this.userCryptoKey = userCryptoKey;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(Map<String, Object> userDetailMap, String month) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(userCryptoKey, userDetailMap, month);
		if (projectFileDescription != null) {
			Map<String, Map<String, List<Integer>>> data = (Map) gitLocal.getFile(projectFileDescription);
			if (data != null)
				return data;
		}
		return Collections.emptyMap();
	}


}
