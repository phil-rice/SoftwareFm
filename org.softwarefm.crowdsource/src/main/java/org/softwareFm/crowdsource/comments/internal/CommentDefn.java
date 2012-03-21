package org.softwareFm.crowdsource.comments.internal;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.git.IFileDescription;

public class CommentDefn implements ICommentDefn {

	private final IFileDescription fileDescription;
	/** If this a reply to a previous comment, then the replyIndex is the line number of the old comment. If a new comment, it is -1 */
	private final int replyIndex;

	public CommentDefn(IFileDescription fileDescription, int replyIndex) {
		this.fileDescription = fileDescription;
		this.replyIndex = replyIndex;
	}

	@Override
	public String toString() {
		return "CommentDefn [replyIndex=" + replyIndex + ", fileDescription=" + fileDescription + "]";
	}

	@Override
	public IFileDescription fileDescription() {
		return fileDescription;
	}

	@Override
	public int replyIndex() {
		return replyIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileDescription == null) ? 0 : fileDescription.hashCode());
		result = prime * result + replyIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommentDefn other = (CommentDefn) obj;
		if (fileDescription == null) {
			if (other.fileDescription != null)
				return false;
		} else if (!fileDescription.equals(other.fileDescription))
			return false;
		if (replyIndex != other.replyIndex)
			return false;
		return true;
	}

}
