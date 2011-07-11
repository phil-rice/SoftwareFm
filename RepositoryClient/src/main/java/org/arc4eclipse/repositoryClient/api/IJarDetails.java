package org.arc4eclipse.repositoryClient.api;

import org.arc4eclipse.repositoryClient.api.impl.JarDetails;

public interface IJarDetails {

	String pathToJar();

	String release();

	public static class Utils {

		public static IJarDetails makeJarDetails(String pathToJar, String release) {
			return new JarDetails(pathToJar, release);
		}
	}

}
