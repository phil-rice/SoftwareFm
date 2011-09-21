package org.softwareFm.jdtBinding.api;

import java.util.LinkedHashMap;

import org.softwareFm.utilities.callbacks.ICallback;

public class RippedResult extends LinkedHashMap<String, Object> {

	public RippedResult(String hexDigest, String jarPath, String jarName, String javadoc, String source, ICallback<String> javadocMutator, ICallback<String> sourceMutator) {
		put(JdtConstants.jarNameKey, jarName);
		put(JdtConstants.javadocKey, javadoc);
		put(JdtConstants.sourceKey, source);
		put(JdtConstants.jarPathKey, jarPath);
		put(JdtConstants.hexDigestKey, hexDigest);
		put(JdtConstants.javadocMutatorKey, javadocMutator);
		put(JdtConstants.sourceMutatorKey, sourceMutator);
	}

}
