/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.selection.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.eclipse.cache.CachedArtifactData;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.future.Futures;
import org.softwarefm.utilities.runnable.Runnables;

/** I have tried to keep this eclipse agnostic. */
public class SelectedArtifactSelectionManager<S, N> implements ISelectedBindingManager<S> {
	private final AtomicInteger currentSelectionCount = new AtomicInteger();
	private ISelectedBindingStrategy<S, N> strategy;
	private final ExecutorService executor;
	private final ISelectedBindingListenerAndAdderRemover<S> listenerManager;
	private final ICallback<Throwable> exceptionHandler;
	private final IArtifactDataCache cache;
	private S lastSelection;

	public SelectedArtifactSelectionManager(final ISelectedBindingListenerAndAdderRemover<S> listenerManager, final ISelectedBindingStrategy<S, N> strategy, ExecutorService executor, IArtifactDataCache cache, ICallback<Throwable> exceptionHandler) {
		this.listenerManager = listenerManager;
		this.cache = cache;
		this.exceptionHandler = exceptionHandler;
		this.strategy = new ISelectedBindingStrategy<S, N>() {
			public ArtifactData findArtifact(S selection, FileAndDigest fileAndDigest, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;
				ArtifactData artifactData = strategy.findArtifact(selection, fileAndDigest, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (fileAndDigest.digest != null)
					if (artifactData == null)
						listenerManager.unknownDigest(fileAndDigest, selectionCount);
					else
						listenerManager.artifactDetermined(artifactData, selectionCount);
				return artifactData;
			}

			public N findNode(S selection, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;
				return strategy.findNode(selection, selectionCount);
			}

			public CodeData findExpressionData(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				CodeData codeData = strategy.findExpressionData(selection, node, selectionCount);
				listenerManager.codeSelectionOccured(codeData, selectionCount);
				return codeData;
			}

			public File findFile(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				File file = strategy.findFile(selection, node, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (file == null)
					listenerManager.notJavaElement(selectionCount);
				return file;
			}

			public FileAndDigest findDigest(S selection, N node, File file, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				FileAndDigest fileAndDigest = strategy.findDigest(selection, node, file, selectionCount);
				if (fileAndDigest == null)
					listenerManager.notInAJar(file, selectionCount);
				else
					listenerManager.digestDetermined(fileAndDigest, selectionCount);
				return fileAndDigest;
			}
		};
		this.executor = executor;
	}

	public Future<?> selectionOccured(S selection) {
		this.lastSelection = selection;
		final int thisSelectionCount = currentSelectionCount.incrementAndGet();
		return selectionRaw(selection, thisSelectionCount);
	}

	private Future<?> selectionRaw(S selection, final int thisSelectionCount) {
		if (selection == null) {
			listenerManager.notJavaElement(thisSelectionCount);
			return Futures.doneFuture(null);
		} else {
			final N node = strategy.findNode(selection, thisSelectionCount);
			return executor.submit(task(selection, node, thisSelectionCount));
		}
	}

	@SuppressWarnings("unused")
	Runnable task(final S selection, final N node, final int thisSelectionCount) {
		CodeData codeData = strategy.findExpressionData(selection, node, thisSelectionCount);
		final File file = strategy.findFile(selection, node, thisSelectionCount);
		if (file != null) {
			CachedArtifactData cachedArtifactData = cache.projectData(file);
			if (cachedArtifactData == null)
				return new Runnable() {
					public void run() {
						try {
							FileAndDigest fileAndDigest = strategy.findDigest(selection, node, file, thisSelectionCount);
							if (fileAndDigest != null && fileAndDigest.digest != null) {
								ArtifactData artifactData = strategy.findArtifact(selection, fileAndDigest, thisSelectionCount);
								if (thisSelectionCount == currentSelectionId())
									if (artifactData != null)
										cache.addProjectData(artifactData);
									else
										cache.addNotFound(fileAndDigest);
							}
						} catch (RuntimeException e) {
							ICallback.Utils.call(exceptionHandler, e);
						}
					}
				};
			if (cachedArtifactData.found())
				listenerManager.artifactDetermined(cachedArtifactData.artifactData, thisSelectionCount);
			else
				listenerManager.unknownDigest(cachedArtifactData.fileAndDigest, thisSelectionCount);
		}
		return Runnables.noRunnable;
	}

	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listenerManager.addSelectedArtifactSelectionListener(listener);
	}

	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listenerManager.removeSelectedArtifactSelectionListener(listener);
	}

	public int currentSelectionId() {
		return currentSelectionCount.get();
	}

	public void reselect(int selectionId) {
		int currentSelectionId = currentSelectionId();
		if (selectionId == currentSelectionId)
			selectionRaw(lastSelection, selectionId);

	}

	public void dispose() {
		listenerManager.dispose();
	}

	public List<ISelectedBindingListener> listeners() {
		return Collections.unmodifiableList(listenerManager.listeners());
	}

}