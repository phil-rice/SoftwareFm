package org.softwareFm.server;

import java.util.Map;

public interface ILocalGitClient extends ILocalGitClientReader {
	/** This will delete the file */
	void delete(IFileDescription fileDescription);

	/** Map should just have simple values: strings, numbers, arrays of strings/numbers */
	void post(IFileDescription fileDescription, Map<String, Object> map);

}
