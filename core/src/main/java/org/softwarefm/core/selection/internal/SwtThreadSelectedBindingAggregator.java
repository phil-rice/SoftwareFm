package org.softwarefm.core.selection.internal;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class SwtThreadSelectedBindingAggregator<S> implements ISelectedBindingListenerAndAdderRemover<S> {

	private final IListenerList<ISelectedBindingListener> listeners;
	private final Display display;

	public SwtThreadSelectedBindingAggregator(Display display, IMultipleListenerList listenerList) {
		this.display = display;
		this.listeners = IListenerList.Utils.list(listenerList, ISelectedBindingListener.class, this);
	}

	public void codeSelectionOccured(final CodeData codeData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.codeSelectionOccured(codeData, selectionCount);
					}
				});
			}
		});
	}

	public void notJavaElement(final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.notJavaElement(selectionCount);
					}
				});
			}
		});
	}

	public void digestDetermined(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.digestDetermined(fileAndDigest, selectionCount);
					}
				});
			}
		});
	}

	public void notInAJar(final File file, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.notInAJar(file, selectionCount);
					}
				});
			}
		});
	}

	public void artifactDetermined(final ArtifactData artifactData, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.artifactDetermined(artifactData, selectionCount);
					}
				});
			}
		});
	}

	public void unknownDigest(final FileAndDigest fileAndDigest, final int selectionCount) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.unknownDigest(fileAndDigest, selectionCount);
					}
				});
			}
		});
	}

	@Override
	public void friendsArtifactUsage(final ArtifactData artifactData, final Map<FriendData, UsageStatData> friendsUsage) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.friendsArtifactUsage(artifactData, friendsUsage);
					}
				});
			}
		});
	}

	@Override
	public void friendsCodeUsage(final CodeData codeData, final Map<FriendData, UsageStatData> friendsUsage) {
		display.asyncExec(new Runnable() {
			public void run() {
				listeners.fire(new ICallback<ISelectedBindingListener>() {
					@Override
					public void process(ISelectedBindingListener t) throws Exception {
						t.friendsCodeUsage(codeData, friendsUsage);
					}
				});
			}
		});
	}

	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.addListener(listener);
	}

	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listeners.removeListener(listener);
	}

	public void dispose() {
		listeners.clear();
	}

	public IListenerList<ISelectedBindingListener> getListeners() {
		return listeners;
	}

	public boolean isValid() {
		return true;
	}

}
