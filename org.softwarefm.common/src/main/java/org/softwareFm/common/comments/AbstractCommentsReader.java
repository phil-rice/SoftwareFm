package org.softwareFm.common.comments;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommentConstants;
import org.softwareFm.common.url.Urls;

public abstract class AbstractCommentsReader implements ICommentsReader {

	@Override
	public List<Map<String, Object>> commentsFor(String url, List<String> uuids) {
		List<Map<String, Object>> result = Lists.newList();
		for (String filePrefix : Lists.append(uuids, CommentConstants.globalCommentsName)) {
			String fullUrl = Urls.compose(url, filePrefix+"." + CommentConstants.commentExtension);
			result.add(getListFromFile(fullUrl));
		}
		return result;
	}

	abstract protected Map<String, Object> getListFromFile(String fullUrl);

}
