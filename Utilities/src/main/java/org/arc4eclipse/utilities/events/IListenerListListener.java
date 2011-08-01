package org.arc4eclipse.utilities.events;

import java.util.List;

import org.arc4eclipse.utilities.callbacks.ICallback;

public interface IListenerListListener {

	<L, T> void eventOccured(List<L> Listener, ICallback<T> callback);

}
