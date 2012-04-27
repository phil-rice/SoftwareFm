/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IFactory;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.comparators.Comparators;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

@SuppressWarnings("unchecked")
public class Container implements IContainerBuilder, IUserAndGroupsContainer {

	private final Map<Class<?>, Object> map = Maps.newMap();
	private final ITransactionManager transactionManager;
	private final IGitOperations gitOperations;
	private final AtomicInteger taskCount = new AtomicInteger();

	public Container(ITransactionManager transactionManager, IGitOperations gitOperations) {
		this.transactionManager = transactionManager;
		this.gitOperations = gitOperations;
	}

	@Override
	public ITransactionManager getTransactionManager() {
		return transactionManager;
	}

	@Override
	public int activeJobs() {
		return transactionManager.activeJobs();
	}

	@Override
	public <T, X extends T> void register(Class<T> class1, X x) {
		map.put(class1, x);
	}

	@Override
	public <T, X extends T> void register(Class<T> class1, Callable<X> x) {
		map.put(class1, x);
	}

	@Override
	public <T, X extends T> void register(Class<T> class1, IFactory<T> factory) {
		map.put(class1, factory);
	}

	@Override
	public <Result, API> ITransaction<Result> access(Class<API> clazz, IFunction1<API, Result> job) {
		return accessWithCallbackFn(clazz, job, Functions.<Result, Result> identity());
	}

	@Override
	public <Result, Intermediate, API> ITransaction<Result> accessWithCallbackFn(final Class<API> clazz, final IFunction1<API, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Intermediate>() {
			@Override
			public Intermediate apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				return job.apply(readWriter);
			}

			@Override
			public String toString() {
				return "accessWithCallbackFn(" + clazz.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, resultCallback, readWriter);
	}

	@Override
	public <API> ITransaction<Void> access(final Class<API> clazz, final ICallback<API> job) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				job.process(readWriter);
				return null;
			}

			@Override
			public String toString() {
				return "access(" + clazz.getSimpleName() + ", " + job + ")";
			}
		}, Functions.<Void, Void> identity(), readWriter);
	}

	@Override
	public <A1, A2> ITransaction<Void> access(final Class<A1> clazz1, final Class<A2> clazz2, final ICallback2<A1, A2> job) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				job.process(one, two);
				return null;
			}

			@Override
			public String toString() {
				return "access(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + job + ")";
			}
		}, Functions.<Void, Void> identity(), one, two);
	}

	@Override
	public <Result, Intermediate, A1, A2> ITransaction<Result> accessWithCallbackFn(final Class<A1> clazz1, final Class<A2> clazz2, final IFunction2<A1, A2, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Intermediate>() {
			@Override
			public Intermediate apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				return job.apply(one, two);
			}

			@Override
			public String toString() {
				return "accessWithCallbackFn(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, resultCallback, one, two);
	}

	@Override
	public <Result, Intermediate, A1, A2, A3> ITransaction<Result> accessWithCallbackFn(final Class<A1> clazz1, final Class<A2> clazz2, final Class<A3> clazz3, final IFunction3<A1, A2, A3, Intermediate> job, final IFunction1<Intermediate, Result> resultCallback) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		final A3 three = getReadWriter(clazz3);
		return transactionManager.start(new IFunction1<IMonitor, Intermediate>() {
			@Override
			public Intermediate apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				return job.apply(one, two, three);
			}

			@Override
			public String toString() {
				return "accessWithCallbackFn(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + clazz3.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, resultCallback, one, two);
	}

	@Override
	public <A1, A2, A3> ITransaction<Void> access(final Class<A1> clazz1, final Class<A2> clazz2, final Class<A3> clazz3, final ICallback3<A1, A2, A3> job) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		final A3 three = getReadWriter(clazz3);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				job.process(one, two, three);
				return null;
			}

			@Override
			public String toString() {
				return "access(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + clazz3.getSimpleName() + ", " + job + ")";
			}
		}, Functions.<Void, Void> identity(), one, two, three);

	}

	@Override
	public <Result, API> ITransaction<Result> accessWithCallback(final Class<API> clazz, final IFunction1<API, Result> job, final ICallback<Result> resultCallback) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = job.apply(readWriter);
				return result;
			}

			@Override
			public String toString() {
				return "accessWithCallback(" + clazz.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, Functions.identityWithCallback(resultCallback), readWriter);
	}

	@Override
	public <Result, A1, A2> ITransaction<Result> accessWithCallback(final Class<A1> clazz1, final Class<A2> clazz2, final IFunction2<A1, A2, Result> job, final ICallback<Result> resultCallback) {
		final A1 one = getReader(clazz1);
		final A2 two = getReader(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = job.apply(one, two);
				return result;
			}

			@Override
			public String toString() {
				return "access(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, Functions.identityWithCallback(resultCallback), one, two);
	}

	@Override
	public <Result, A1, A2, A3> ITransaction<Result> accessWithCallback(final Class<A1> clazz1, final Class<A2> clazz2, final Class<A3> clazz3, final IFunction3<A1, A2, A3, Result> job, final ICallback<Result> resultCallback) {
		final A1 one = getReader(clazz1);
		final A2 two = getReader(clazz2);
		final A3 three = getReader(clazz3);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = job.apply(one, two, three);
				return result;
			}

			@Override
			public String toString() {
				return "accessWithCallback(" + clazz1.getSimpleName() + ", " + clazz2.getSimpleName() + ", " + clazz3.getSimpleName() + ", " + job + ", " + resultCallback + ")";
			}
		}, Functions.identityWithCallback(resultCallback), one, two, three);
	}

	private <API> API getReadWriter(Class<API> clazz) {
		try {
			Object object = map.get(clazz);
			if (object == null)
				throw new NullPointerException(MessageFormat.format(CommonMessages.cannotAccessModifyWithoutRegisteredReader, "modify", "readWriter", clazz, Lists.sort(map.keySet(), Comparators.classComporator())));
			if (object instanceof IFactory<?>)
				return ((IFactory<API>) object).build();
			if (object instanceof Callable<?>)
				return ((Callable<API>) object).call();
			if (!clazz.isAssignableFrom(object.getClass()))
				throw new IllegalStateException(MessageFormat.format(CommonMessages.readWriterSetUpIncorrectly, object.getClass().getName(), object));
			return (API) object;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private <API> API getReader(Class<API> clazz) {
		Object reader = map.get(clazz);
		if (reader == null)
			throw new NullPointerException(MessageFormat.format(CommonMessages.cannotAccessModifyWithoutRegisteredReader, "access", "reader", clazz, Lists.sort(map.keySet(), Comparators.classComporator())));
		if (!clazz.isAssignableFrom(reader.getClass()))
			throw new IllegalStateException(MessageFormat.format(CommonMessages.readWriterSetUpIncorrectly, reader.getClass().getName(), reader));
		return (API) reader;
	}

	@Override
	public ITransactionManager transactionManager() {
		return transactionManager;
	}

	@Override
	public <T> ITransaction<T> accessGroupReader(IFunction1<IGroupsReader, T> function, ICallback<T> resultCallback) {
		return accessWithCallback(IGroupsReader.class, function, resultCallback);
	}

	@Override
	public <T> ITransaction<T> accessUserReader(IFunction1<IUserReader, T> function, ICallback<T> resultCallback) {
		return accessWithCallback(IUserReader.class, function, resultCallback);
	}

	@Override
	public <T> ITransaction<T> accessUserMembershipReader(IFunction2<IGroupsReader, IUserMembershipReader, T> function, ICallback<T> resultCallback) {
		return accessWithCallback(IGroupsReader.class, IUserMembershipReader.class, function, resultCallback);
	}

	@Override
	public ITransaction<Void> accessUser(ICallback<IUser> callback) {
		return access(IUser.class, callback);
	}

	@Override
	public ITransaction<Void> accessGroups(ICallback<IGroups> callback) {
		return access(IGroups.class, callback);
	}

	@Override
	public ITransaction<Void> accessUserMembership(ICallback2<IGroups, IUserMembership> callback) {
		return access(IGroups.class, IUserMembership.class, callback);
	}

	@Override
	public IGitOperations gitOperations() {
		return gitOperations;
	}

	private String getTaskName() {
		return "Task: " + taskCount.getAndIncrement();
	}
}