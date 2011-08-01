package org.arc4eclipse.utilities.events;

import java.util.List;

import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;

public class ListenerListListenerMock implements IListenerListListener {

	public List<String> callbacks = Lists.newList();

	@Override
	public <L, T> void eventOccured(List<L> listeners, ICallback<T> callback) {
		callbacks.add(listeners + ": " + callback.toString());
	}
}
