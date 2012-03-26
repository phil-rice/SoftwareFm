package org.softwareFm.crowdsource.api.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainerBuilder;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
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

@SuppressWarnings("unchecked")
abstract public class AbstractCrowdSourceReadWriterApi implements IContainerBuilder, IUserAndGroupsContainer {

	private final Map<Class<?>, Object> map = Maps.newMap();

	@Override
	public <API> void modify(Class<API> clazz, ICallback<API> callback) {
		Object readWriter = getReadWriter(clazz);
		ICallback.Utils.call(callback, (API) readWriter);
	}

	private <API> API getReadWriter(Class<API> clazz) {
		Object readWriter = map.get(clazz);
		if (readWriter == null)
			throw new NullPointerException(MessageFormat.format(CommonMessages.cannotAccessModifyWithoutRegisteredReader, "modify", "readWriter", clazz, Lists.sort(map.keySet(), Comparators.classComporator())));
		if (!clazz.isAssignableFrom(readWriter.getClass()))
			throw new IllegalStateException(MessageFormat.format(CommonMessages.readWriterSetUpIncorrectly, readWriter.getClass().getName(), readWriter));
		return (API) readWriter;
	}

	@Override
	public <Result, API> Result access(Class<API> clazz, IFunction1<API, Result> function) {
		Object reader = getReader(clazz);
		return Functions.call(function, (API) reader);
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
	public <T, X extends T> void register(Class<T> class1, X x) {
		map.put(class1, x);
	}

	@Override
	public <T> T accessUserReader(IFunction1<IUserReader, T> function) {
		return access(IUserReader.class, function);
	}

	@Override
	public void modifyUser(ICallback<IUser> callback) {
		modify(IUser.class, callback);
	}

	@Override
	public <T> T accessGitReader(IFunction1<IGitReader, T> function) {
		return access(IGitReader.class, function);

	}

	@Override
	public <T> T accessGroupReader(IFunction1<IGroupsReader, T> function) {
		return access(IGroupsReader.class, function);
	}

	@Override
	public void modifyGroups(ICallback<IGroups> callback) {
		modify(IGroups.class, callback);
	}

	@Override
	public <T> T accessUserMembershipReader(IFunction2<IGroupsReader, IUserMembershipReader, T> function) {
		return access(IGroupsReader.class, IUserMembershipReader.class, function);
	}

	@Override
	public void modifyUserMembership(ICallback2<IGroups, IUserMembership> callback) {
		modify(IGroups.class, IUserMembership.class, callback);
	}

	@Override
	public void modifyComments(ICallback<IComments> callback) {
		modify(IComments.class, callback);
	}

	@Override
	public <T> T accessCommentsReader(IFunction1<ICommentsReader, T> function) {
		return access(ICommentsReader.class, function);
	}

	@Override
	public <A1, A2> void modify(Class<A1> clazz1, Class<A2> clazz2, ICallback2<A1, A2> callback) {
		A1 one = getReadWriter(clazz1);
		A2 two = getReadWriter(clazz2);
		ICallback2.Utils.call(callback, one, two);
	}

	@Override
	public <A1, A2, A3> void modify(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, ICallback3<A1, A2, A3> callback) {
		A1 one = getReadWriter(clazz1);
		A2 two = getReadWriter(clazz2);
		A3 three = getReadWriter(clazz3);
		ICallback3.Utils.call(callback, one, two, three);

	}

	@Override
	public <Result, A1, A2> Result access(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Result> function) {
		A1 one = getReader(clazz1);
		A2 two = getReader(clazz2);
		return Functions.call(function, one, two);
	}

	@Override
	public <Result, A1, A2, A3> Result access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Result> function) {
		A1 one = getReader(clazz1);
		A2 two = getReader(clazz2);
		A3 three = getReader(clazz3);
		return Functions.call(function, one, two, three);
	}

}
