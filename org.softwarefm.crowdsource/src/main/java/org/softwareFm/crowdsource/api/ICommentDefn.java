package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.comments.internal.CommentDefn;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface ICommentDefn {
	IFileDescription fileDescription();

	/** If this a reply to a previous comment, then the replyIndex is the line number of the old comment. If a new comment, it is -1 */
	int replyIndex();

	public static class Utils {
		public static ICommentDefn everyoneInitial(String url) {
			return everyoneReply(url, -1);
		}

		public static ICommentDefn everyoneReply(String url, int replyIndex) {
			return new CommentDefn(IFileDescription.Utils.plain(url, CommentConstants.globalCommentsFile), replyIndex);
		}

		public static ICommentDefn myInitial(IUserAndGroupsContainer container, String softwareFmId, String crypto, String url) {
			return myReply(container, softwareFmId, crypto, url, -1);
		}

		public static ICommentDefn myReply(IUserAndGroupsContainer container, final String softwareFmId, final String crypto, final String url, final int replyIndex) {
			return container.accessUserReader(new IFunction1<IUserReader, ICommentDefn>() {
				@Override
				public ICommentDefn apply(IUserReader userReader) throws Exception {
					String commentCrypto = userReader.getUserProperty(softwareFmId, crypto, CommentConstants.commentCryptoKey);
					// if (commentCrypto == null) This can be null without this being an exception: when this is the first comment that you have made
					// throw new NullPointerException(MessageFormat.format(CommentConstants.cannotGetCommentsCrypto, "softwareFmId", softwareFmId));
					return new CommentDefn(IFileDescription.Utils.encrypted(url, softwareFmId + "." + CommentConstants.commentExtension, commentCrypto), replyIndex);
				}
			});
		}

		public static ICommentDefn groupInitial(IUserAndGroupsContainer container, String groupId, String groupCrypto, String url) {
			return groupReply(container, groupId, groupCrypto, url, -1);
		}

		public static ICommentDefn groupReply(IUserAndGroupsContainer container, final String groupId, final String groupCrypto, final String url, final int replyIndex) {
			return container.accessGroupReader(new IFunction1<IGroupsReader, ICommentDefn>() {
				@Override
				public ICommentDefn apply(IGroupsReader groupsReader) throws Exception {
					String commentCrypto = groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);
					// if (commentCrypto == null)
					// throw new NullPointerException(MessageFormat.format(CommentConstants.cannotGetCommentsCrypto, "groupId", groupId));
					return new CommentDefn(IFileDescription.Utils.encrypted(url, groupId + "." + CommentConstants.commentExtension, commentCrypto), replyIndex);
				}
			});
		}
	}

}
