package org.arc4eclipse.repositoryClient.paths;

import org.arc4eclipse.repositoryClient.api.IJarDetails;

public interface IPathCalculatorThin {
	String getProjectPath(IJarDetails jarDetails);

	String getReleasePath(IJarDetails jarDetails);

	String getPackagePath(IJarDetails jarDetails, String packageName);

	String getClassPath(IJarDetails jarDetails, String packageName, String... classNames);

	String getMethodPath(IJarDetails jarDetails, String packageName, String... classNamesAndMethodName);

}