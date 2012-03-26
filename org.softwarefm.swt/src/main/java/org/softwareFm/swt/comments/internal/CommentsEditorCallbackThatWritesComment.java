package org.softwareFm.swt.comments.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.swt.comments.ICommentsEditorCallback;

public class CommentsEditorCallbackThatWritesComment implements ICommentsEditorCallback {
	private final String softwareFmId;
	private final String userCrypto;
	private final List<Map<String, Object>> groupsData;
	private final Runnable whenFinished;
	private final IUserAndGroupsContainer container;

	public CommentsEditorCallbackThatWritesComment(IUserAndGroupsContainer container,  String softwareFmId, String userCrypto, Iterable<Map<String, Object>> groupsData, Runnable whenFinished) {
		this.container = container;
		this.softwareFmId = softwareFmId;
		this.userCrypto = userCrypto;
		this.groupsData= Iterables.list(groupsData);
		this.whenFinished = whenFinished;
	}

	@Override
	public void youComment(String url, String text) {
		ICommentDefn defn = ICommentDefn.Utils.myInitial(container, softwareFmId, userCrypto, url);
		IComments.Utils.addComment(container, softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void groupComment(String url, int groupIndex, String text) {
		Map<String, Object> data = groupsData.get(groupIndex);
		String groupId = (String) data.get(GroupConstants.groupIdKey);
		String groupCrypto = (String) data.get(GroupConstants.groupCryptoKey);
		if (groupId == null || groupCrypto == null)
			throw new IllegalStateException(data.toString());
		ICommentDefn defn = ICommentDefn.Utils.groupInitial(container, groupId, groupCrypto, url);
		IComments.Utils.addComment(container, softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void everyoneComment(String url, String text) {
		ICommentDefn defn = ICommentDefn.Utils.everyoneInitial(url);
		IComments.Utils.addComment(container, softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void cancel() {
		whenFinished.run();
	}
}