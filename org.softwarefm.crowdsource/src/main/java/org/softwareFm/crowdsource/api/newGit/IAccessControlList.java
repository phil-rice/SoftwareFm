package org.softwareFm.crowdsource.api.newGit;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.exceptions.ACLException;
import org.softwareFm.crowdsource.api.newGit.internal.AcceptAllAccessControlList;
import org.softwareFm.crowdsource.api.newGit.remote.internal.SimpleAccessControlList;

/** These methods throw an exception if the operation is not permitted. */
public interface IAccessControlList {

	void read(String rl) throws ACLException;

	void write(String rl) throws ACLException;

	void delete(String rl) throws ACLException;

	void updateListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException;

	void deleteListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException;

	public static class Utils {

		public static IAccessControlList noAccessControl() {
			return new AcceptAllAccessControlList();
		}
		public static IAccessControlList accessControlFor(UserData userData, String...writePrefixes) {
			return new SimpleAccessControlList(userData, writePrefixes);
		}
	}
}
