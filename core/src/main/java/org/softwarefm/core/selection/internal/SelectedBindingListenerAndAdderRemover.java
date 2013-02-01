package org.softwarefm.core.selection.internal;

import java.io.File;
import java.util.Map;

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

 class SelectedBindingListenerAndAdderRemover<S> implements ISelectedBindingListenerAndAdderRemover<S> {

	private final IListenerList<ISelectedBindingListener> list;

	public SelectedBindingListenerAndAdderRemover(IMultipleListenerList list) {
		this.list = IListenerList.Utils.list(list, ISelectedBindingListener.class, this);
	}

	@Override
	public void codeSelectionOccured(final int selectionCount, final CodeData codeData) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.codeSelectionOccured(selectionCount, codeData);
			}

			@Override
			public String toString() {
				return "codeSelectedOccured(" + selectionCount + "," + codeData + ")";
			}
		});
	}

	@Override
	public void notJavaElement(final int selectionCount) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.notJavaElement(selectionCount);
			}

			@Override
			public String toString() {
				return "notJavaElement(" + selectionCount + ")";
			}
		});
	}

	@Override
	public void digestDetermined(final int selectionCount, final FileAndDigest fileAndDigest) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.digestDetermined(selectionCount, fileAndDigest);
			}

			@Override
			public String toString() {
				return "digestDetermined(" + selectionCount + "," + fileAndDigest + ")";
			}
		});
	}

	@Override
	public void notInAJar(final int selectionCount, final File file) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.notInAJar(selectionCount, file);
			}

			@Override
			public String toString() {
				return "notInAJar(" + selectionCount + "," + file + ")";
			}
		});
	}

	@Override
	public void artifactDetermined(final int selectionCount, final ArtifactData artifactData) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.artifactDetermined(selectionCount, artifactData);
			}

			@Override
			public String toString() {
				return "artifactDetermined(" + selectionCount + "," + artifactData + ")";
			}
		});
	}

	@Override
	public void unknownDigest(final int selectionCount, final FileAndDigest fileAndDigest) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.unknownDigest(selectionCount, fileAndDigest);
			}

			@Override
			public String toString() {
				return "unknownDigest(" + selectionCount + "," + fileAndDigest + ")";
			}
		});
	}

	@Override
	public void friendsArtifactUsage(final ArtifactData artifactData, final Map<FriendData, UsageStatData> friendsUsage) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.friendsArtifactUsage(artifactData, friendsUsage);
			}

			@Override
			public String toString() {
				return "friendsArtifactUsage(" + artifactData + "," + friendsUsage + ")";
			}
		});
	}

	@Override
	public void friendsCodeUsage(final CodeData codeData, final Map<FriendData, UsageStatData> friendsUsage) {
		list.fire(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener t) throws Exception {
				t.friendsCodeUsage(codeData, friendsUsage);
			}

			@Override
			public String toString() {
				return "friendsCodeUsage(" + codeData + "," + friendsUsage + ")";
			}
		});
	}



	@Override
	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		list.addListener(listener);

	}

	@Override
	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		list.removeListener(listener);
	}

	@Override
	public IListenerList<ISelectedBindingListener> getListeners() {
		return list;
	}

	@Override
	public void dispose() {
	}

	@Override
	public String toString() {
		return "[SelectedBindingListenerAndAdderRemover]";
	}

}
