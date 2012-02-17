package org.softwareFm.swt.comments;

import java.text.MessageFormat;

import org.softwareFm.common.IGitLocal;
import org.softwareFm.eclipse.comments.AbstractCommentsReader;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public class CommentsReaderLocal extends AbstractCommentsReader {

	private final String softwareFmId;
	private final String commentCrypto;

	public CommentsReaderLocal(IGitLocal gitLocal, IUserMembershipReader userMembershipReader, String softwareFmId, String commentCrypto) {
		super(gitLocal, userMembershipReader);
		this.softwareFmId = softwareFmId;
		this.commentCrypto = commentCrypto;
	}

	@Override
	protected String getCommentCryptoFor(String softwareFmId) {
		if (!this.softwareFmId.equals(softwareFmId))
			throw new IllegalArgumentException(MessageFormat.format(CommentConstants.invalidSoftwareFmId, this.softwareFmId, softwareFmId));
		return commentCrypto;
	}

}
