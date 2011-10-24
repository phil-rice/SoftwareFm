package org.softwareFm.display.displayer;

import java.text.MessageFormat;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.strings.PreAndPost;
import org.softwareFm.utilities.strings.Strings;

public class RippedEditorId {

	public final String editorId;
	public final String entity;
	public final String key;
	private boolean raw;

	public RippedEditorId(String editorId) {
		this.editorId = editorId;
		if (isData()) {
			String path = editorId.substring(5);
			PreAndPost firstSplit = Strings.split(path, '.');
			if (firstSplit.post == null)
				throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.illegalPath, path));
			raw = "raw".equals(firstSplit.pre);
			if (raw) {
				PreAndPost entityAndKey = Strings.split(firstSplit.post, '.');
				entity = entityAndKey.pre;
				key = entityAndKey.post;
			} else {
				entity = firstSplit.pre;
				key = firstSplit.post;
			}
		} else {
			entity = null;
			key = null;
		}

	}

	public boolean isData() {
		return editorId != null && editorId.startsWith("data.");
	}

	public boolean isRaw() {
		return raw;
	}

	@Override
	public String toString() {
		return "RippedEditorId [editorId=" + editorId + ", entity=" + entity + ", key=" + key + ", raw=" + raw + "]";
	}

}
