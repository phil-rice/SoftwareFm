package org.arc4eclipse.repositoryClient.api;

import java.io.File;

import org.arc4eclipse.repositoryClient.api.impl.JarDetails;
import org.arc4eclipse.utilities.collections.Files;

public interface IJarDetails {

	File pathToJar();

	String release();

	byte[] digest();

	String digestAsHexString();

	public static class Utils {

		public static IJarDetails makeJarDetails(String pathToJar, String release) {
			return makeJarDetails(new File(pathToJar), release);
		}

		public static IJarDetails makeJarDetails(File pathToJar, String release) {
			return new JarDetails(pathToJar, null, release);
		}

		public static IJarDetails withDigest(IJarDetails raw) {
			if (raw.digest() != null)
				return raw;
			else
				return new JarDetails(raw.pathToJar(), Files.digest(raw.pathToJar()), raw.release());
		}
	}

}
