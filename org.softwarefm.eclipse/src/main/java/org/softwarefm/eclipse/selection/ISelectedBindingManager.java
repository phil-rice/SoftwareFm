package org.softwarefm.eclipse.selection;

import java.util.concurrent.Future;

public interface ISelectedBindingManager<S> extends ISelectedBindingListenerAdderAndRemover {

	Future<?> selectionOccured(S selection);

}
