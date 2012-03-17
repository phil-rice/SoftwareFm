package org.softwareFm.swt.comments.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.swt.comments.ICommentWriter;
import org.softwareFm.swt.comments.ICommentsEditorCallback;

public class CommentsEditorCallbackThatWritesComment implements ICommentsEditorCallback {
	private final String softwareFmId;
	private final String userCrypto;
	private final ICommentWriter commentWriter;
	private final List<Map<String, Object>> groupsData;
	private final IGroupsReader reader;
	private final Runnable whenFinished;
	private final IUserReader userReader;

	public CommentsEditorCallbackThatWritesComment(IUserReader userReader, IGroupsReader reader, ICommentWriter commentWriter, String softwareFmId, String userCrypto, Iterable<Map<String, Object>> groupsData, Runnable whenFinished) {
		this.softwareFmId = softwareFmId;
		this.userCrypto = userCrypto;
		this.commentWriter = commentWriter;
		this.groupsData= Iterables.list(groupsData);
		this.reader = reader;
		this.userReader = userReader;
		this.whenFinished = whenFinished;
	}

	@Override
	public void youComment(String url, String text) {
		ICommentDefn defn = ICommentDefn.Utils.myInitial(userReader, softwareFmId, userCrypto, url);
		commentWriter.addComment(softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void groupComment(String url, int groupIndex, String text) {
		Map<String, Object> data = groupsData.get(groupIndex);
		String groupId = (String) data.get(GroupConstants.groupIdKey);
		String groupCrypto = (String) data.get(GroupConstants.groupCryptoKey);
		if (groupId == null || groupCrypto == null)
			throw new IllegalStateException(data.toString());
		ICommentDefn defn = ICommentDefn.Utils.groupInitial(reader, groupId, groupCrypto, url);
		commentWriter.addComment(softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void everyoneComment(String url, String text) {
		ICommentDefn defn = ICommentDefn.Utils.everyoneInitial(url);
		commentWriter.addComment(softwareFmId, userCrypto, defn, text);
		whenFinished.run();
	}

	@Override
	public void cancel() {
		whenFinished.run();
	}
}