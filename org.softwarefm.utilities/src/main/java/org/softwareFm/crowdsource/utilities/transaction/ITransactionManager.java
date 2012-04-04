package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.Future;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IShutdown;
import org.softwareFm.crowdsource.utilities.transaction.internal.TransactionManager;

public interface ITransactionManager extends IShutdown {

	/** The job is executed (probably in a different thread. The result callback is called with the result. potentialTransactions are added to the list of transactionals if they implements ITransactional. Transactionals are either commited or rollbacked after the result callback has been called. */
	<Intermediate, Result> ITransaction<Result> start(IFunction1<IMonitor, Intermediate> job, IFunction1<Intermediate, Result> resultCallback, Object... potentialTransactionals);

	/** Adds a resource to the transaction: this will be commited or rollbacked when the transaction concludes */
	<T> void addResource(ITransaction<T> transaction, ITransactional transactional);

	public static class Utils {
		public static ITransactionManagerBuilder standard() {
			return new TransactionManager(IServiceExecutor.Utils.defaultExecutor("ITransactionManager-{0}"), new TransactionManager.DefaultFutureToTransactionDn());
		}

		/** This is used when (for example) you want to do something while waiting in the get method: such as process swt dispatch thread queues */
		public static ITransactionManagerBuilder withFutureToTransactionFn(IFunction1<Future<?>, ITransaction<?>> fn) {
			return new TransactionManager(IServiceExecutor.Utils.defaultExecutor("ITransactionManager-{0}"), fn);
		}
	}
}
