package org.softwareFm.crowdsource.api.newGit;


public interface ISingleSource {
	String url();

	String source();

	/** can return null, if so any sources "me" and "mygroup" will not be accessible */
	String userId();

	String userCrypto();
}
