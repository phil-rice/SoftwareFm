package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.project.ProjectForServer;

public interface IProject {

	/** group -> artifact -> list of the days */
	Map<String, Map<String, List<Integer>>> getProjectDetails(Map<String, Object> userDetailMap, String month);

	void addProjectDetails(Map<String, Object> userDetailMap, String groupId, String artifactId, String month, long day);

	public static class Utils {
		public static IProject makeProjectForServer(IGitOperations gitOperations, IFunction1<Map<String, Object>, String> cryptoFn, IUser user, IUrlGenerator userUrlGenerator, Callable<String> cryptoGenerator) {
			return new ProjectForServer(gitOperations, cryptoFn, user, userUrlGenerator, cryptoGenerator);
		}

		public static IFileDescription makeProjectFileDescription(IUrlGenerator userGenerator, Map<String, Object> userDetailMap, String month, String userCryptoKey) {
			String url = userGenerator.findUrlFor(userDetailMap);
			String projectUrl = Urls.compose(url, SoftwareFmConstants.projectDirectoryName);
			IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, userCryptoKey);
			return fileDescription;
		}
	}

}
