package org.arc4eclipse.repositoryClient.paths.impl;

import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.paths.IJarPathToRepositoryPathConvertor;
import org.arc4eclipse.repositoryClient.paths.IPathCalculatorThin;
import org.arc4eclipse.utilities.exceptions.WrappedException;

public class PathCalculatorThin implements IPathCalculatorThin {

	private final IJarPathToRepositoryPathConvertor convertor;

	public PathCalculatorThin() {
		this.convertor = new JarPathToRepositoryPathConvertor();
	}

	public PathCalculatorThin(IJarPathToRepositoryPathConvertor convertor) {
		this.convertor = convertor;
	}

	public String getProjectPath(IJarDetails jarDetails) {
		return "/data/" + getJarDetailsPath(jarDetails);
	}

	private String getJarDetailsPath(IJarDetails jarDetails) {
		try {
			return convertor.apply(jarDetails);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public String getReleasePath(IJarDetails jarDetails) {
		return "/data/" + getJarDetailsPath(jarDetails);
	}

	public String getPackagePath(IJarDetails jarDetails, String packageName) {
		return "/data/" + getJarDetailsPath(jarDetails) + "/" + packageName;
	}

	public String getClassPath(IJarDetails jarDetails, String packageName, String... classNames) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPackagePath(jarDetails, packageName));
		for (String className : classNames)
			builder.append("/" + className);
		String result = builder.toString();
		return result;
	}

	public String getMethodPath(IJarDetails jarDetails, String packageName, String... classNamesAndMethodName) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPackagePath(jarDetails, packageName));
		for (int i = 0; i < classNamesAndMethodName.length - 1; i++)
			builder.append("/" + classNamesAndMethodName[i]);
		builder.append("/methods/");
		builder.append(classNamesAndMethodName[classNamesAndMethodName.length - 1]);
		String getMethodPath = builder.toString();
		return getMethodPath;
	}

}
