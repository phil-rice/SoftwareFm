/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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