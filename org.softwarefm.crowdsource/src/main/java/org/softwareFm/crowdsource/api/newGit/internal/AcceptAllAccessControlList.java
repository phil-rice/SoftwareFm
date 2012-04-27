package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.ISingleRowReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.exceptions.ACLException;

public class AcceptAllAccessControlList implements IAccessControlList {

	@Override
	public void read(String rl) throws ACLException {
	}

	@Override
	public void write(String rl) throws ACLException {
	}

	@Override
	public void delete(String rl) throws ACLException {
	}

	@Override
	public void updateListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException {
	}

	@Override
	public void deleteListItem(ISingleRowReader reader, ISingleSource source, int index) throws ACLException {
	}

}
