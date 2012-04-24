/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.crowdsource.utilities.services.IShutdown;
import org.softwareFm.crowdsource.utilities.transaction.internal.TransactionManager;

public interface ITransactionManager extends IShutdown {
	public static Logger logger = Logger.getLogger(ITransactionManager.class);

	/**
	 * The job is executed (probably in a different thread. The result callback is called with the result. potentialTransactions are added to the list of transactionals if they implements ITransactional. Transactionals are either commited or rollbacked after the result callback has been called. <br />
	 * 
	 * If the job throws RedoTransactionException, the transaction will be retried. The retry strategy is up to the implementation, but should deal with poisoned jobs. If resultCallback throws RedoTransactionException, it is an exception that will cause the transaction to be rolled back.<br />
	 * 
	 * Note that as the creator of the job, you cannot be sure if the job will be executed multiple times, so ensure that it has no side effects. Sideeffects go in the result callback: thats what it is for
	 */
	<Intermediate, Result> ITransaction<Result> start(IFunction1<IMonitor, Intermediate> job, IFunction1<Intermediate, Result> resultCallback, Object... potentialTransactionals);

	/** Adds a resource to the transaction: this will be commited or rollbacked when the transaction concludes */
	<T> void addResource(ITransaction<T> transaction, ITransactional transactional);

	int activeJobs();

	/** is the calling thread part of a transaction */
	boolean inTransaction();

	public static class Utils {
		public static AtomicInteger count = new AtomicInteger();

		public static ITransactionManagerBuilder standard(int workerThreads, long timeOutMs) {
			IServiceExecutor serviceExecutor = IServiceExecutor.Utils.defaultExecutor("ITransactionManager" + count.getAndIncrement() + "-{0}", workerThreads);
			return new TransactionManager(serviceExecutor, new TransactionManager.DefaultFutureToTransactionDn(timeOutMs));
		}

		/** This is used when (for example) you want to do something while waiting in the get method: such as process swt dispatch thread queues */
		public static ITransactionManagerBuilder withFutureToTransactionFn(int workerThreads, IFunction1<Future<?>, ITransaction<?>> fn) {
			return new TransactionManager(IServiceExecutor.Utils.defaultExecutor("ITransactionManager" + count.getAndIncrement() + "-{0}", workerThreads), fn);
		}
	}
}