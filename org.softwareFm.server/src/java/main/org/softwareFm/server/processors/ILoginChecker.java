package org.softwareFm.server.processors;

import java.util.Map;

public interface ILoginChecker {

	/** returns null if failed, crypto and softwareFmId if succeeded */
	Map<String,String> login(String email, String passwordHash);

}
