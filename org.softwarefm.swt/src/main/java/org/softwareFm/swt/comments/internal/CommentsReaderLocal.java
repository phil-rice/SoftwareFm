package org.softwareFm.swt.comments.internal;

import java.util.Map;

import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.comments.AbstractCommentsReader;

public class CommentsReaderLocal extends AbstractCommentsReader {

	private final IGitLocal gitLocal;
	public CommentsReaderLocal(IGitLocal gitLocal) {
		this.gitLocal = gitLocal;
	}
	@Override
	protected Map<String, Object> getListFromFile(String fullUrl) {
		gitLocal.getFileAsString(fileDescription)
		return null;
	}

}
