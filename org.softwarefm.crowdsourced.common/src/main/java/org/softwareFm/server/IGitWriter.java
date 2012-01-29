package org.softwareFm.server;

import java.util.Map;

public interface IGitWriter {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);
}
