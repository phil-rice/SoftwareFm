package org.softwareFm.server.processors;

public interface ISaltProcessor {

	String makeSalt();

	void invalidateSalt(String salt);

	boolean checkAndInvalidateSalt(String salt);
}
