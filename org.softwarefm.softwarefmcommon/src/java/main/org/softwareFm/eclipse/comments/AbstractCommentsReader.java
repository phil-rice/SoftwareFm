package org.softwareFm.eclipse.comments;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitReader;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public abstract class AbstractCommentsReader implements ICommentsReader {

	private final IGitReader gitReader;
	private final IUserMembershipReader userMembershipReader;
	private final IUserReader userReader;
	private final IGroupsReader groupsReader;

	public AbstractCommentsReader(IGitReader gitReader, IUserReader userReader, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader) {
		this.gitReader = gitReader;
		this.userReader = userReader;
		this.userMembershipReader = userMembershipReader;
		this.groupsReader = groupsReader;
	}

	@Override
	public List<Map<String, Object>> globalComments(String baseUrl, String source) {
		IFileDescription fd = IFileDescription.Utils.plain(baseUrl, CommentConstants.globalCommentsFile);
		List<Map<String, Object>> result = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
		return result;
	}

	@Override
	public List<Map<String, Object>> groupComments(String baseUrl, String softwareFmId, String userCrypto) {
		List<Map<String, Object>> result = Lists.newList();
		List<Map<String, Object>> allGroupData = userMembershipReader.walkGroupsFor(softwareFmId, userCrypto);
		for (Map<String, Object> groupData : allGroupData) {
			String groupId = (String) groupData.get(GroupConstants.groupIdKey);
			String groupCrypto = (String) groupData.get(GroupConstants.groupCryptoKey);
			if (groupId == null || groupCrypto == null)
				throw new IllegalStateException(MessageFormat.format(GroupConstants.missingDataFromMembership, softwareFmId, groupData));
			String commentCrypto = groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);
			String groupName = groupsReader.getGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey);
			if (commentCrypto != null) {//it is entirely possible to belong to a group for which no one has made a comment
				IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, groupId + "." + CommentConstants.commentExtension, commentCrypto);
				List<Map<String, Object>> maps = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, groupName));
				result.addAll(maps);
			}
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> myComments(String baseUrl, String softwareFmId, String userCrypto, String source) {
		String commentCrypto = userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
		IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, softwareFmId + "." + CommentConstants.commentExtension, commentCrypto);
		List<Map<String, Object>> result = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
		return result;
	}

}
