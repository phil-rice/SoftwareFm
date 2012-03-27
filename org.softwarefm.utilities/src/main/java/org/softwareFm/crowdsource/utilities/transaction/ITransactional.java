package org.softwareFm.crowdsource.utilities.transaction;

public interface ITransactional {

	void commit();

	void rollback();
}
