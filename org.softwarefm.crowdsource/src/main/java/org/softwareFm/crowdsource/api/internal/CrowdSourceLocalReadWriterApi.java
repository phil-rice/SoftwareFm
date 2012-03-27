package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;


public class CrowdSourceLocalReadWriterApi extends Container{

	public CrowdSourceLocalReadWriterApi(ITransactionManager transactionManager, IGitOperations gitOperations, long timeOutMs) {
		super(transactionManager, gitOperations);
	}



}
