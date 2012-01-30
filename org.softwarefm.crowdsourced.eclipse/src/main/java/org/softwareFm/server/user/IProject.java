package org.softwareFm.server.user;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.IUser;
import org.softwareFm.server.processors.internal.ProjectForServer;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.Urls;

public interface IProject {

	Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month);

	void addProjectDetails(Map<String, Object> userDetailMap, String groupId, String artifactId, String month, long day);

	public static class Utils {
		public static IProject makeProjectForServer(IGitOperations gitOperations, IFunction1<Map<String, Object>, String> cryptoFn, IUser user, IUrlGenerator userUrlGenerator, Callable<String> cryptoGenerator) {
			return new ProjectForServer(gitOperations, cryptoFn, user, userUrlGenerator, cryptoGenerator);
		}
		
		public static IFileDescription makeProjectFileDescription(IUrlGenerator userGenerator, Map<String,Object> userDetailMap, String month, String userCryptoKey){
			String url = userGenerator.findUrlFor(userDetailMap);
			String projectUrl = Urls.compose(url, SoftwareFmConstants.projectDirectoryName);
			IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, userCryptoKey);
			return fileDescription;
		}
	}

}
