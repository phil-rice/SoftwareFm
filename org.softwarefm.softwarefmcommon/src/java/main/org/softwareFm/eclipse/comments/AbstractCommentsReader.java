package org.softwareFm.eclipse.comments;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public abstract class AbstractCommentsReader implements ICommentsReader {

	private final IGitReader gitReader;
	private final IUserMembershipReader userMembershipReader;

	public AbstractCommentsReader(IGitReader gitReader, IUserMembershipReader userMembershipReader) {
		this.gitReader = gitReader;
		this.userMembershipReader = userMembershipReader;
	}
	
	abstract protected String getCommentCryptoFor(String softwareFmId);

	@Override
	public List<Map<String, Object>> globalComments(String baseUrl) {
		IFileDescription fd = IFileDescription.Utils.plain(baseUrl, CommentConstants.globalCommentsFile);
		List<Map<String, Object>> result = gitReader.getFileAsListOfMaps(fd);
		return result;
	}

	@Override
	public List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId) {
		List<Map<String, Object>> result = Lists.newList();
		List<Map<String, Object>> allGroupData = userMembershipReader.walkGroupsFor(softwareFmId);
		for (Map<String, Object> groupData : allGroupData) {
			String groupId = (String) groupData.get(GroupConstants.groupIdKey);
			String groupCrypto = (String) groupData.get(GroupConstants.groupCryptoKey);
			if (groupId == null || groupCrypto == null)
				throw new IllegalStateException(MessageFormat.format(GroupConstants.missingDataFromMembership, softwareFmId, groupData));
			IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, groupId + "." + CommentConstants.commentExtension, groupCrypto);
			List<Map<String, Object>> maps = gitReader.getFileAsListOfMaps(fd);
			result.addAll(maps);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> myComments(String baseUrl, String softwareFmId) {
		String crypto = getCommentCryptoFor(softwareFmId);
		IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, softwareFmId +"." + CommentConstants.commentExtension, crypto);
		return gitReader.getFileAsListOfMaps(fd);
	}

}
