package org.softwareFm.utilities.events;

import java.util.List;

import org.softwareFm.utilities.callbacks.ICallback;

public interface IListenerListListener {

	<L, T> void eventOccured(List<L> Listener, ICallback<T> callback);

}
