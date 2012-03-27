package org.softwareFm.crowdsource.utilities.transaction;


import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IShutdown;
import org.softwareFm.crowdsource.utilities.transaction.internal.TransactionManager;

public interface ITransactionManager extends IShutdown{

	/** The job is executed (probably in a different thread. The result callback is called with the result. Transactionals are either commited or rollbacked after the result callback has been called. */
	<T> ITransaction<T> start(IFunction1<IMonitor, T> job, ICallback<T> resultCallback, ITransactional... transactionals);

	/** Adds a resource to the transaction: this will be commited or rollbacked when the transaction concludes */
	<T> void addResource(ITransaction<T> transaction, ITransactional transactional);

	public static class Utils {
		public static ITransactionManager standard(){
			return new TransactionManager(IServiceExecutor.Utils.defaultExecutor());
		}
	}
}
