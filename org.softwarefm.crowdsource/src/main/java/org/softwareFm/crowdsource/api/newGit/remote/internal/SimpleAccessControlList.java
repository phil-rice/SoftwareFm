package org.softwareFm.crowdsource.api.newGit.remote.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.ISingleRowReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.exceptions.ACLException;
import org.softwareFm.crowdsource.api.newGit.exceptions.AclMessages;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotDeleteException;
import org.softwareFm.crowdsource.api.newGit.exceptions.CannotWriteException;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;

/**
 * <ul>
 * <li>Allows read access to everything (encryption is our main security model for user data).
 * <li>Allows write access to urls begining with certain defined values
 * <li>If a line item has a user attached to it, only that user can edit / delete the data
 * </ul>
 * 
 */
public class SimpleAccessControlList implements IAccessControlList {

	private final List<String> prefixes;
	private final UserData userData;

	public SimpleAccessControlList(UserData userData, String... prefixes) {
		this.userData = userData;
		this.prefixes = Arrays.asList(prefixes);
	}

	@Override
	public void read(String rl) throws ACLException {
	}

	@Override
	public void write(String rl) throws ACLException {
		for (String prefix : prefixes)
			if (rl.startsWith(prefix))
				return;
		throw new CannotWriteException(MessageFormat.format(AclMessages.cannotWriteException, rl));
	}

	@Override
	public void delete(String rl) throws ACLException {
		throw new CannotDeleteException(MessageFormat.format(AclMessages.cannotDeleteException, rl));
	}

	@Override
	public void updateListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException {
		Map<String, Object> row = reader.readRow(source, index);
		Object userId = row.get(LoginConstants.softwareFmIdKey);
		if (userId != null && !userId.equals(userData.softwareFmId))
			throw new CannotWriteException(MessageFormat.format(AclMessages.cannotUpdateListItemException, source.fullRl(), index, userData.softwareFmId, userId));
	}

	@Override
	public void deleteListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException {
		Map<String, Object> row = reader.readRow(source, index);
		Object userId = row.get(LoginConstants.softwareFmIdKey);
		if (userId != null && !userId.equals(userData.softwareFmId))
			throw new CannotWriteException(MessageFormat.format(AclMessages.cannotDeleteListItemException, source.fullRl(), index, userData.softwareFmId, userId));
	}

}
