package org.softwarefm.eclipse.selection;

import java.util.concurrent.Future;

import org.softwarefm.utilities.future.Futures;

public interface ISelectedBindingManager<S> extends ISelectedBindingListenerAdderAndRemover {

	Future<?> selectionOccured(S selection);

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
			};
		}
	}

}
