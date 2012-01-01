package org.softwareFm.server;

import java.util.Map;

public interface ILocalGitClient extends ILocalGitClientReader {
	/** This will delete the node at the Url */
	void delete(String url);

	/** Map should just have simple values: strings, numbers, arrays of strings/numbers */
	void post(String url, Map<String, Object> map);

}
