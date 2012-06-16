package org.softwarefm.eclipse.selection.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.utilities.collections.Lists;

public class SwtThreadSelectedBindingAggregator<S> implements ISelectedBindingListenerAndAdderRemover<S> {

	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final Display display;

	public SwtThreadSelectedBindingAggregator(Display display) {
		this.display = display;
	}

	public void classAndMethodSelectionOccured(final ExpressionData expressionData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.classAndMethodSelectionOccured(expressionData, selectionCount);

			}
		});
	}

	public void notJavaElement(final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.notJavaElement(selectionCount);

			}
		});
	}

	public void digestDetermined(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.digestDetermined(fileAndDigest, selectionCount);
			}
		});
	}

	public void notInAJar(final File file, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.notInAJar(file, selectionCount);
			}
		});
	}

	public void projectDetermined(final ProjectData projectData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.projectDetermined(projectData, selectionCount);
			}
		});
	}

	public void unknownDigest(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.unknownDigest(fileAndDigest, selectionCount);
			}
		});
	}

	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.add(listener);
	}

	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.remove(listener);
	}

	public void dispose() {
		listeners.clear();
	}

	public List<ISelectedBindingListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

}
