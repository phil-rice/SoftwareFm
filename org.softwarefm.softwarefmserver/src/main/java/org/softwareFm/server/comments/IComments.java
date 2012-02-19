package org.softwareFm.server.comments;

import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.comments.ICommentsReader;

public interface IComments extends ICommentsReader {

	void add(String softwareFmId,  String userCrypto, ICommentDefn defn, String text);
}
