package org.softwareFm.jdtBinding.api;

import java.util.LinkedHashMap;

public class RippedResult extends LinkedHashMap<String, Object> {

	public RippedResult(String hexDigest, String project, String jarPath, String jarName, String javadoc, String source, IJavadocSourceMutator javadocMutator, IJavadocSourceMutator sourceMutator) {
		put(JdtConstants.jarNameKey, jarName);
		put(JdtConstants.javadocKey, javadoc);
		put(JdtConstants.sourceKey, source);
		put(JdtConstants.javaProjectKey, project);
		put(JdtConstants.jarPathKey, jarPath);
		put(JdtConstants.hexDigestKey, hexDigest);
		put(JdtConstants.javadocMutatorKey, javadocMutator);
		put(JdtConstants.sourceMutatorKey, sourceMutator);
	}

}
