package org.softwarefm.utilities.events.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.events.IValid;
import org.softwarefm.utilities.exceptions.MultipleExceptions;
import org.softwarefm.utilities.maps.Maps;

public class MultipleListenerList implements IMultipleListenerList {

	final Map<Object, List<Object>> map = Collections.synchronizedMap(new HashMap<Object, List<Object>>());

	public <L> void addListener(Object source, L listener) {
		Maps.addToList(map, source, listener);
	}

	@SuppressWarnings("unchecked")
	public <L> void fire(Object source, ICallback<L> callback) {
		List<Throwable> throwables = new ArrayList<Throwable>();
		List<L> list = (List<L>) Maps.getOrEmptyList(map, source);
		for (Iterator<L> iterator = list.iterator(); iterator.hasNext();)
			try {
				L listener = iterator.next();
				if (listener instanceof IValid)
					if (!((IValid) listener).isValid()) {
						iterator.remove();
						continue;
					}
				callback.process(listener);
			} catch (ThreadDeath e) {
				throw e;
			} catch (Throwable e) {
				throwables.add(e);
			}
		if (throwables.size() > 0)
			throw new MultipleExceptions(throwables);
	}

}
