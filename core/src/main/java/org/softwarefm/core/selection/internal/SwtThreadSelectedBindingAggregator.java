package org.softwarefm.core.selection.internal;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.collections.Lists;

public class SwtThreadSelectedBindingAggregator<S> implements ISelectedBindingListenerAndAdderRemover<S> {

	private final List<ISelectedBindingListener> listeners = Lists.newList();
	private final Display display;

	public SwtThreadSelectedBindingAggregator(Display display) {
		this.display = display;
	}

	public void codeSelectionOccured(final CodeData codeData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.codeSelectionOccured(codeData, selectionCount);

			}
		});
	}

	public void notJavaElement(final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.notJavaElement(selectionCount);

			}
		});
	}

	public void digestDetermined(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.digestDetermined(fileAndDigest, selectionCount);
			}
		});
	}

	public void notInAJar(final File file, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.notInAJar(file, selectionCount);
			}
		});
	}

	public void artifactDetermined(final ArtifactData artifactData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.artifactDetermined(artifactData, selectionCount);
			}
		});
	}

	public void unknownDigest(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.unknownDigest(fileAndDigest, selectionCount);
			}
		});
	}

	@Override
	public void friendsArtifactUsage(final ArtifactData artifactData, final Map<FriendData, UsageStatData> friendsUsage) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.friendsArtifactUsage(artifactData, friendsUsage);
			}
		});
	}

	@Override
	public void friendsCodeUsage(final CodeData codeData, final Map<FriendData, UsageStatData> friendsUsage) {
		display.asyncExec(new Runnable() {
			public void run() {
				checkListeners();
				for (ISelectedBindingListener listener : listeners)
					listener.friendsCodeUsage(codeData, friendsUsage);
			}
		});
	}

	protected void checkListeners() {
		assert Thread.currentThread() == display.getThread();
		for (Iterator<ISelectedBindingListener> iterator = listeners.iterator(); iterator.hasNext();) {
			ISelectedBindingListener listener = iterator.next();
			if (listener.invalid())
				iterator.remove();
		}

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

	public boolean invalid() {
		return false;
	}

	public List<ISelectedBindingListener> listeners() {
		return Collections.unmodifiableList(listeners);
	}

}
