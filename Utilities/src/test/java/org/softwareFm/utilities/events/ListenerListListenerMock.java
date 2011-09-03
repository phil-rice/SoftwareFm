package org.softwareFm.utilities.events;

import java.util.List;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class ListenerListListenerMock implements IListenerListListener {

	public List<String> callbacks = Lists.newList();

	@Override
	public <L, T> void eventOccured(List<L> listeners, ICallback<T> callback) {
		callbacks.add(listeners + ": " + callback.toString());
	}
}
