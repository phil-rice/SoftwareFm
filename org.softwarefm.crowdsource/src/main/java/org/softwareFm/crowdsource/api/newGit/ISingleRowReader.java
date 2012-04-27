package org.softwareFm.crowdsource.api.newGit;

import java.util.Map;

public interface ISingleRowReader {
	Map<String, Object> readRow(ISingleSource singleSource, int row);

}
