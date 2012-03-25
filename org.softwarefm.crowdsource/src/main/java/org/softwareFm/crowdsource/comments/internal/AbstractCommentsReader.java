package org.softwareFm.crowdsource.comments.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public abstract class AbstractCommentsReader implements ICommentsReader {

	protected final IContainer api;

	public AbstractCommentsReader(IContainer api) {
		this.api = api;
	}

	@Override
	public List<Map<String, Object>> globalComments(String baseUrl, final String source) {
		final IFileDescription fd = IFileDescription.Utils.plain(baseUrl, CommentConstants.globalCommentsFile);
		return api.accessGitReader(new IFunction1<IGitReader, List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> apply(IGitReader gitReader) throws Exception {
				List<Map<String, Object>> result = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
				return result;
			}
		});
	}

	@Override
	public List<Map<String, Object>> groupComments(final String baseUrl, final String softwareFmId, final String userCrypto) {
		return api.access(IGitReader.class, IGroupsReader.class, IUserMembershipReader.class, new IFunction3<IGitReader, IGroupsReader, IUserMembershipReader, List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> apply(IGitReader gitReader, IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) {
				final List<Map<String, Object>> result = Lists.newList();
				Iterable<Map<String, Object>> allGroupData = userMembershipReader.walkGroupsFor(softwareFmId, userCrypto);
				for (Map<String, Object> groupData : allGroupData) {
					String groupId = (String) groupData.get(GroupConstants.groupIdKey);
					String groupCrypto = (String) groupData.get(GroupConstants.groupCryptoKey);
					if (groupId == null || groupCrypto == null)
						throw new IllegalStateException(MessageFormat.format(GroupConstants.missingDataFromMembership, softwareFmId, groupData));
					String commentCrypto = groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);
					String groupName = groupsReader.getGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey);
					if (commentCrypto != null) {// it is entirely possible to belong to a group for which no one has made a comment
						IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, groupId + "." + CommentConstants.commentExtension, commentCrypto);
						List<Map<String, Object>> maps = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, groupName));
						result.addAll(maps);
					}
				}
				return result;
			}
		});
	}

	@Override
	public List<Map<String, Object>> myComments(final String baseUrl, final String softwareFmId, final String userCrypto, final String source) {
		return api.accessGitReader(new IFunction1<IGitReader, List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> apply(final IGitReader gitReader) throws Exception {
				return api.accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> apply(IGroupsReader from1, IUserMembershipReader from2) {
						return api.accessUserReader(new IFunction1<IUserReader, List<Map<String, Object>>>() {
							@Override
							public List<Map<String, Object>> apply(IUserReader userReader) throws Exception {
								String commentCrypto = userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
								IFileDescription fd = IFileDescription.Utils.encrypted(baseUrl, softwareFmId + "." + CommentConstants.commentExtension, commentCrypto);
								List<Map<String, Object>> result = Lists.map(gitReader.getFileAsListOfMaps(fd), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
								return result;
							}
						});
					}
				});
			}

		});
	}
}
