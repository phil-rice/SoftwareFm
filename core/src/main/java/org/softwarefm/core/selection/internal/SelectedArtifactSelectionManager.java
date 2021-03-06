/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.core.selection.internal;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.cache.internal.CachedArtifactData;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.future.Futures;
import org.softwarefm.utilities.runnable.Runnables;

/** I have tried to keep this eclipse agnostic. */
public class SelectedArtifactSelectionManager<S, N> extends SelectedBindingListenerAndAdderRemover<S> implements ISelectedBindingManager<S> {
	private final AtomicInteger currentSelectionCount = new AtomicInteger();
	private ISelectedBindingStrategy<S, N> strategy;
	private final ExecutorService executor;
	private final ICallback<Throwable> exceptionHandler;
	private final IArtifactDataCache cache;
	private S lastSelection;

	public SelectedArtifactSelectionManager(final IMultipleListenerList listenerList, final ISelectedBindingStrategy<S, N> strategy, ExecutorService executor, IArtifactDataCache cache, ICallback<Throwable> exceptionHandler) {
		super(listenerList);
		this.cache = cache;
		this.exceptionHandler = exceptionHandler;
		this.executor = executor;
		if (executor == null)
			throw new NullPointerException();
		this.strategy = new ISelectedBindingStrategy<S, N>() {
			@Override
			public ArtifactData findArtifact(S selection, FileAndDigest fileAndDigest, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;

				ArtifactData artifactData = strategy.findArtifact(selection, fileAndDigest, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (fileAndDigest.digest != null)
					if (artifactData == null) {
						unknownDigest(selectionCount, fileAndDigest);
					} else
						artifactDetermined(selectionCount, artifactData);
				return artifactData;
			}

			@Override
			public N findNode(S selection, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;
				return strategy.findNode(selection, selectionCount);
			}

			@Override
			public CodeData findExpressionData(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				CodeData codeData = strategy.findExpressionData(selection, node, selectionCount);
				codeSelectionOccured(selectionCount, codeData);
				return codeData;
			}

			@Override
			public File findFile(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				File file = strategy.findFile(selection, node, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (file == null)
					notJavaElement(selectionCount);
				return file;
			}

			@Override
			public FileAndDigest findDigest(S selection, N node, File file, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				FileAndDigest fileAndDigest = strategy.findDigest(selection, node, file, selectionCount);
				if (fileAndDigest == null)
					notInAJar(selectionCount, file);
				else
					digestDetermined(selectionCount, fileAndDigest);
				return fileAndDigest;
			}
		};
	}

	@Override
	public Future<?> selectionOccured(S selection) {
		this.lastSelection = selection;
		final int thisSelectionCount = currentSelectionCount.incrementAndGet();
		return selectionRaw(selection, thisSelectionCount);
	}

	private Future<?> selectionRaw(S selection, final int thisSelectionCount) {
		if (selection == null) {
			notJavaElement(thisSelectionCount);
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
					@Override
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
				artifactDetermined(thisSelectionCount, cachedArtifactData.artifactData);
			else
				unknownDigest(thisSelectionCount, cachedArtifactData.fileAndDigest);
		}
		return Runnables.noRunnable;
	}

	@Override
	public int currentSelectionId() {
		return currentSelectionCount.get();
	}

	@Override
	public void reselect(int selectionId) {
		int currentSelectionId = currentSelectionId();
		if (selectionId == currentSelectionId)
			selectionRaw(lastSelection, selectionId);

	}

}