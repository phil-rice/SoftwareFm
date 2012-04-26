/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

/**
 * The container has similarities to a standard Inversion of Control Container: it holds objects and manages their life cycle<br />
 * 
 * This container however is designed to work in a gui. For gui's such as SWT or Swing, only 1 thread can manipulate the display, and if that thread is blocked while other activities occur the gui is slowed. Given this constraint on the world, this container encourages all the work to be undertaken on a worker thread, and if the gui needs to be updated, an intermediate object describing how the gui is change is built up, then the gui changed in a callback<br />
 * Because the contents of the container are only accessible off the swing thread, it is fairly straightforward to ensure that the time consuming work is handled by worker threads.
 * 
 * IMPORTANT The container is built on top of a transaction manager. Should the worker thread throw RedoTransactionException, the task passed to the worker thread will be redone. The job passed to the worker thread may be executed many times, and should therefore either be a pure function, or work with the transaction manager.
 * 
 */
public interface IContainer {
	ITransactionManager transactionManager();

	int activeJobs();

	ITransactionManager getTransactionManager();

	<API> ITransaction<Void> access(Class<API> clazz, ICallback<API> job);

	<A1, A2> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, ICallback2<A1, A2> job);

	<A1, A2, A3> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, ICallback3<A1, A2, A3> job);

	<Result, API> ITransaction<Result> access(Class<API> clazz, IFunction1<API, Result> job);

	<Result, API> ITransaction<Result> accessWithCallback(Class<API> clazz, IFunction1<API, Result> job, ICallback<Result> resultCallback);

	<Result, A1, A2> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Result> job, ICallback<Result> resultCallback);

	<Result, A1, A2, A3> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Result> job, ICallback<Result> resultCallback);

	<Result, Intermediate, A1, A2> ITransaction<Result> accessWithCallbackFn(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Intermediate> job, IFunction1<Intermediate, Result> resultCallback);

	<Result, Intermediate, API> ITransaction<Result> accessWithCallbackFn(Class<API> clazz, IFunction1<API, Intermediate> job, IFunction1<Intermediate, Result> resultCallback);

	<Result, Intermediate, A1, A2, A3> ITransaction<Result> accessWithCallbackFn(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Intermediate> job, IFunction1<Intermediate, Result> resultCallback);

	IGitOperations gitOperations();

}