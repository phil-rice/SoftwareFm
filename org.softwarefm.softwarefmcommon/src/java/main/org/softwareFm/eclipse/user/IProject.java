package org.softwareFm.eclipse.user;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public interface IProject extends IProjectReader {

	void addProjectDetails(Map<String, Object> userDetailMap, String groupId, String artifactId, String month, long day);

	public static class Utils {

		public static IFileDescription makeProjectFileDescription(IUrlGenerator userGenerator, Map<String, Object> userDetailMap, String month, String userCryptoKey) {
			String url = userGenerator.findUrlFor(userDetailMap);
			String projectUrl = Urls.compose(url, SoftwareFmConstants.projectDirectoryName);
			IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, userCryptoKey);
			return fileDescription;
		}
	}

}
