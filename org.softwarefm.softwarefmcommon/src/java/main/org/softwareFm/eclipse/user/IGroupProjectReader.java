package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGroupReader;

public interface IGroupProjectReader extends IGroupReader{
	/** group -> artifact -> person -> list of the days */
	Map<String, Map<String, Map<String, List<Integer>>>> getProjectDetails(Map<String, Object> groupDetailMap, String month);
}
