package org.softwareFm.jdtBinding.api;

import org.softwareFm.utilities.callbacks.ICallback;

public class RippedResult {

	public final String hexDigest;
	public final String jarPath;
	public final String javadoc;
	public final String source;
	public final ICallback<String> javadocMutator;
	public final ICallback<String> sourceMutator;

	public RippedResult(String hexDigest, String jarPath, String javadoc, String source, ICallback<String> javadocMutator, ICallback<String> sourceMutator) {
		this.hexDigest = hexDigest;
		this.jarPath = jarPath;
		this.javadoc = javadoc;
		this.source = source;
		this.javadocMutator = javadocMutator;
		this.sourceMutator = sourceMutator;
	}

}
