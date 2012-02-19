package org.softwareFm.eclipse.comments;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.eclipse.comments.internal.CommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;

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

		public static ICommentDefn myInitial(IUserReader userReader, String softwareFmId, String crypto, String url) {
			return myReply(userReader, softwareFmId, crypto, url, -1);
		}

		public static ICommentDefn myReply(IUserReader userReader, String softwareFmId, String crypto, String url, int replyIndex) {
			String commentCrypto = userReader.getUserProperty(softwareFmId, crypto, CommentConstants.commentCryptoKey);
//			if (commentCrypto == null) This can be null without this being an exception: when this is the first comment that you have made
//				throw new NullPointerException(MessageFormat.format(CommentConstants.cannotGetCommentsCrypto, "softwareFmId", softwareFmId));
			return new CommentDefn(IFileDescription.Utils.encrypted(url, softwareFmId + "." + CommentConstants.commentExtension, commentCrypto), replyIndex);
		}

		public static ICommentDefn groupInitial(IGroupsReader reader, String groupId, String groupCrypto, String url) {
			return groupReply(reader, groupId, groupCrypto, url, -1);
		}

		public static ICommentDefn groupReply(IGroupsReader reader, String groupId, String groupCrypto, String url, int replyIndex) {
			String commentCrypto = reader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);
//			if (commentCrypto == null)
//				throw new NullPointerException(MessageFormat.format(CommentConstants.cannotGetCommentsCrypto, "groupId", groupId));
			return new CommentDefn(IFileDescription.Utils.encrypted(url, groupId + "." + CommentConstants.commentExtension, commentCrypto), replyIndex);
		}
	}

}
