package org.softwarefm.core.selection.internal;

import org.eclipse.swt.widgets.Display;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerListExecutor;
import org.softwarefm.utilities.events.internal.GlobalListenerContext;
import org.softwarefm.utilities.events.internal.GlobalListenerList;

public class SwtThreadExecutor implements IMultipleListenerListExecutor {

	private final Display display;

	public SwtThreadExecutor(Display display) {
		this.display = display;
	}

	@Override
	public <L> void execute(final int eventId, final ICallback<L> callback, final L listener, final GlobalListenerList globalList, final GlobalListenerContext contexts) {
		display.asyncExec(new Runnable() {
			public void run() {
				globalList.fireListenerStart(eventId, contexts, listener);
				ICallback.Utils.call(callback, listener);
				globalList.fireListenerEnd(eventId, contexts, listener);
			}
		});
	}

}
