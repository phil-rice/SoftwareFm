package org.softwareFm.server.processors;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.server.comments.IComments;
import org.softwareFm.server.processors.internal.AbstractCommandProcessor;

public class CommentProcessor extends AbstractCommandProcessor {

	private final IFunction1<Map<String, Object>, String> cryptoFn;
	private final IUserReader userReader;
	private final IUserMembershipReader userMembershipReader;
	private final IGroupsReader groupsReader;
	private final IComments comments;

	public CommentProcessor(IUserReader userReader, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader, IComments comments, IFunction1<Map<String, Object>, String> cryptoFn) {
		super(null, CommonConstants.POST, CommentConstants.commentCommandPrefix);
		this.userReader = userReader;
		this.userMembershipReader = userMembershipReader;
		this.groupsReader = groupsReader;
		this.comments = comments;
		this.cryptoFn = cryptoFn;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, CommentConstants.textKey, CommentConstants.filenameKey);
		String urlWithoutCommand = actualUrl.substring(prefix.length()+1);
		String command = Strings.firstSegment(urlWithoutCommand, "/");
		if (command.equals(CommentConstants.addCommandSuffix)) {
			String url = urlWithoutCommand.substring(CommentConstants.addCommandSuffix.length()+1);
			String userCrypto = Functions.call(cryptoFn, parameters);
			if (userCrypto == null)
				throw new IllegalArgumentException(MessageFormat.format(CommentConstants.illegalSoftwareFmId, parameters));
			ICommentDefn defn = getAddCommentDefn(url, userCrypto, parameters);
			String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
			String encoded = (String) parameters.get(CommentConstants.textKey);
			String text = Crypto.aesDecrypt(userCrypto, encoded);
			comments.add(softwareFmId,userCrypto, defn, text);
			return IProcessResult.Utils.processString("");
		}
		throw new IllegalArgumentException(actualUrl);
	}
//
//	private Map<String, Object> makeData(Map<String, Object> parameters, String userCrypto) {
//		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
//		String encoded = (String) parameters.get(CommentConstants.textKey);
//		String text = Crypto.aesDecrypt(userCrypto, encoded);
//		String moniker = userReader.getUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey);
//		long time = Callables.call(timeCallable);
//		return Maps.stringObjectMap(//
//				LoginConstants.softwareFmIdKey, softwareFmId,//
//				CommentConstants.creatorKey, moniker,//
//				CommentConstants.timeKey, time,//
//				CommentConstants.textKey, text);
//	}

	protected ICommentDefn getAddCommentDefn(String url, String userCrypto, Map<String, Object> parameters) {
		String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String filename = (String) parameters.get(CommentConstants.filenameKey);
		if (filename.equals(CommentConstants.globalCommentsFile))
			return ICommentDefn.Utils.everyoneInitial(url);
		else if (filename.equals(softwareFmId)) {
			String commentCrypto = userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
			if (commentCrypto == null)
				throw new NullPointerException();
			return ICommentDefn.Utils.myInitial(userReader, softwareFmId, userCrypto, url);
		} else {
			for (Map<String, Object> map : userMembershipReader.walkGroupsFor(softwareFmId, userCrypto)) {
				if (filename.equals(map.get(GroupConstants.groupIdKey))) {
					String groupCrypto = (String) map.get(GroupConstants.groupCryptoKey);
					return ICommentDefn.Utils.groupInitial(groupsReader, filename, groupCrypto, url);
				}
			}
		}
		throw new IllegalArgumentException(MessageFormat.format(CommentConstants.cannotWorkOutFileDescription, parameters));
	}
}
