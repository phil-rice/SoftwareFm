package org.softwarefm.eclipse.selection.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.utilities.collections.Lists;

public class SwtThreadSelectedBindingAggregator<S> implements ISelectedBindingListenerAndAdderRemover<S> {

	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final Display display;

	public SwtThreadSelectedBindingAggregator(Display display) {
		this.display = display;
	}

	@Override
	public void classAndMethodSelectionOccured(final ExpressionData expressionData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.classAndMethodSelectionOccured(expressionData, selectionCount);

			}
		});
	}

	@Override
	public void notJavaElement(final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.notJavaElement( selectionCount);
						
			}
		});
	}
	@Override
	public void digestDetermined(final FileNameAndDigest fileNameAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.digestDetermined(fileNameAndDigest, selectionCount);
			}
		});
	}

	@Override
	public void notInAJar(final FileNameAndDigest fileNameAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.notInAJar(fileNameAndDigest, selectionCount);
			}
		});
	}

	@Override
	public void projectDetermined(final ProjectData projectData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.projectDetermined(projectData, selectionCount);
			}
		});
	}

	@Override
	public void unknownDigest(final FileNameAndDigest fileNameAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				for (ISelectedBindingListener listener : listeners)
					listener.unknownDigest(fileNameAndDigest, selectionCount);
			}
		});
	}

	@Override
	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void dispose() {
		listeners.clear();
	}

	public List<ISelectedBindingListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

}
