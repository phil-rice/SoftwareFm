package org.softwarefm.eclipse.selection;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.softwarefm.utilities.future.Futures;

public interface ISelectedBindingManager<S> extends ISelectedBindingListenerAdderAndRemover {

	Future<?> selectionOccured(S selection);
	
	int currentSelectionId();
	
	/** If selectionId == currentSelectionId() this will reselect */
	void reselect(int selectionId);

	public static class Utils {
		public static <S> ISelectedBindingManager<S> noManager() {
			return new ISelectedBindingManager<S>() {

				public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
				}

				public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
				}

				public void dispose() {
				}

				public Future<?> selectionOccured(S selection) {
					return Futures.doneFuture(null);
				}


				public int currentSelectionId() {
					return 0;
				}

				public void reselect(int selectionId) {
				}

				public List<ISelectedBindingListener> listeners() {
					return Collections.emptyList();
				}
			};
		}
	}

}