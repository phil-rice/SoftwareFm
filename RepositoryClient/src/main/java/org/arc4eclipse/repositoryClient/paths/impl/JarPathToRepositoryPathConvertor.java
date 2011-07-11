package org.arc4eclipse.repositoryClient.paths.impl;

import org.arc4eclipse.repositoryClient.api.IJarDetails;
import org.arc4eclipse.repositoryClient.paths.IJarPathToRepositoryPathConvertor;

public class JarPathToRepositoryPathConvertor implements IJarPathToRepositoryPathConvertor {

	public String apply(IJarDetails from) throws Exception {
		return from.pathToJar().replaceAll("\\.", "_");
	}

}
