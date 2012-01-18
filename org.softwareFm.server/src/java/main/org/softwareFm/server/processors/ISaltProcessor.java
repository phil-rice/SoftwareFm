package org.softwareFm.server.processors;

public interface ISaltProcessor {

	String makeSalt();


	boolean invalidateSalt(String salt);
}
