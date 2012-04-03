package org.softwareFm.crowdsource.api.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
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
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

@SuppressWarnings("unchecked")
abstract public class Container implements IContainerBuilder, IUserAndGroupsContainer {

	private final Map<Class<?>, Object> map = Maps.newMap();
	private final ITransactionManager transactionManager;
	private final IGitOperations gitOperations;
	private final AtomicInteger taskCount = new AtomicInteger();

	public Container(ITransactionManager transactionManager, IGitOperations gitOperations) {
		this.transactionManager = transactionManager;
		this.gitOperations = gitOperations;
	}

	@Override
	public <T, X extends T> void register(Class<T> class1, X x) {
		map.put(class1, x);
	}

	@Override
	public <Result, API> ITransaction<Result> access(Class<API> clazz, IFunction1<API, Result> function) {
		return accessWithCallbackFn(clazz, function, Functions.<Result, Result> identity());
	}

	@Override
	public <Result, Intermediate, API> ITransaction<Result> accessWithCallbackFn(Class<API> clazz, final IFunction1<API, Intermediate> function, IFunction1<Intermediate, Result> resultCallback) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Intermediate>() {
			@Override
			public Intermediate apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				return function.apply(readWriter);
			}
		}, resultCallback, readWriter);
	}

	@Override
	public <API> ITransaction<Void> access(Class<API> clazz, final ICallback<API> callback) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				callback.process(readWriter);
				return null;
			}
		}, Functions.<Void, Void> identity(), readWriter);
	}

	@Override
	public <A1, A2> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, final ICallback2<A1, A2> callback) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				callback.process(one, two);
				return null;
			}
		}, Functions.<Void, Void> identity(), one, two);
	}

	@Override
	public <Result, Intermediate, A1, A2> ITransaction<Result> accessWithCallbackFn(Class<A1> clazz1, Class<A2> clazz2, final IFunction2<A1, A2, Intermediate> function, IFunction1<Intermediate, Result> resultCallback) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Intermediate>() {
			@Override
			public Intermediate apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				return function.apply(one, two);
			}
		}, resultCallback, one, two);
	}

	@Override
	public <A1, A2, A3> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, final ICallback3<A1, A2, A3> callback) {
		final A1 one = getReadWriter(clazz1);
		final A2 two = getReadWriter(clazz2);
		final A3 three = getReadWriter(clazz3);
		return transactionManager.start(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				callback.process(one, two, three);
				return null;
			}
		}, Functions.<Void, Void> identity(), one, two, three);

	}

	@Override
	public <Result, API> ITransaction<Result> accessWithCallback(Class<API> clazz, final IFunction1<API, Result> function, final ICallback<Result> resultCallback) {
		final API readWriter = getReadWriter(clazz);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = function.apply(readWriter);
				return result;
			}
		}, Functions.identityWithCallback(resultCallback), readWriter);
	}

	@Override
	public <Result, A1, A2> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, final IFunction2<A1, A2, Result> function, final ICallback<Result> resultCallback) {
		final A1 one = getReader(clazz1);
		final A2 two = getReader(clazz2);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = function.apply(one, two);
				return result;
			}
		}, Functions.identityWithCallback(resultCallback), one, two);
	}

	@Override
	public <Result, A1, A2, A3> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, final IFunction3<A1, A2, A3, Result> function, final ICallback<Result> resultCallback) {
		final A1 one = getReader(clazz1);
		final A2 two = getReader(clazz2);
		final A3 three = getReader(clazz3);
		return transactionManager.start(new IFunction1<IMonitor, Result>() {
			@Override
			public Result apply(IMonitor from) throws Exception {
				from.beginTask(getTaskName(), 1);
				Result result = function.apply(one, two, three);
				return result;
			}
		}, Functions.identityWithCallback(resultCallback), one, two, three);
	}

	private <API> API getReadWriter(Class<API> clazz) {
		Object readWriter = map.get(clazz);
		if (readWriter == null)
			throw new NullPointerException(MessageFormat.format(CommonMessages.cannotAccessModifyWithoutRegisteredReader, "modify", "readWriter", clazz, Lists.sort(map.keySet(), Comparators.classComporator())));
		if (!clazz.isAssignableFrom(readWriter.getClass()))
			throw new IllegalStateException(MessageFormat.format(CommonMessages.readWriterSetUpIncorrectly, readWriter.getClass().getName(), readWriter));
		return (API) readWriter;
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
	public ITransaction<Void> accessComments(ICallback<IComments> callback) {
		return access(IComments.class, callback);
	}

	@Override
	public <T> ITransaction<T> accessGitReader(IFunction1<IGitReader, T> function, ICallback<T> resultCallback) {
		return accessWithCallback(IGitReader.class, function, resultCallback);
	}

	@Override
	public <T> ITransaction<T> accessCommentsReader(IFunction1<ICommentsReader, T> function, ICallback<T> resultCallback) {
		return accessWithCallback(ICommentsReader.class, function, resultCallback);
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
