package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.utilities.transaction.ITransactional;

public class MadeObjectForTestTransactional extends MadeObject implements ITransactional{

	private boolean commitCalled;

	@Override
	public void commit() {
		commitCalled = true;
	}

	@Override
	public void rollback() {
	}

	public boolean commitCalled() {
		return commitCalled;
	}

}
