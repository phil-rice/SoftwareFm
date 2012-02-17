package org.softwareFm.server.comments;

import org.softwareFm.common.IGitReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.eclipse.comments.AbstractCommentsReader;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public class CommentsForServer extends AbstractCommentsReader {

	private final IFunction1<String, String> cryptoFn;
	private final IUserReader user;

	public CommentsForServer(IGitReader gitReader, IUserReader user,IUserMembershipReader userMembershipReader,  IFunction1<String, String> cryptoFn) {
		super(gitReader, userMembershipReader);
		this.user = user;
		this.cryptoFn = cryptoFn;
	}

	@Override
	protected String getCommentCryptoFor(String softwareFmId) {
		String userCrypto = Functions.call(cryptoFn, softwareFmId);
		String commentCrypto = user.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
		return commentCrypto;
	}

}
