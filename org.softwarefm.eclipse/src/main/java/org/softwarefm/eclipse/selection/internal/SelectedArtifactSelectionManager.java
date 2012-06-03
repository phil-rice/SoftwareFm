/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.eclipse.selection.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingManager;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.future.Futures;

/** I have tried to keep this eclipse agnostic. */
public class SelectedArtifactSelectionManager<S, N> implements ISelectedBindingManager<S> {
	private final AtomicInteger currentSelectionCount = new AtomicInteger();
	private ISelectedBindingStrategy<S, N> strategy;
	private final ExecutorService executor;
	private final ISelectedBindingListenerAndAdderRemover<S> listenerManager;
	private final ICallback<Throwable> exceptionHandler;

	public SelectedArtifactSelectionManager(final ISelectedBindingListenerAndAdderRemover<S> listenerManager, final ISelectedBindingStrategy<S, N> strategy, ExecutorService executor, ICallback<Throwable> exceptionHandler) {
		this.listenerManager = listenerManager;
		this.exceptionHandler = exceptionHandler;
		this.strategy = new ISelectedBindingStrategy<S, N>() {
			public ProjectData findProject(S selection, FileNameAndDigest fileNameAndDigest, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;
				ProjectData projectData = strategy.findProject(selection, fileNameAndDigest, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (fileNameAndDigest.digest != null)
					if (projectData == null)
						listenerManager.unknownDigest(fileNameAndDigest, selectionCount);
					else
						listenerManager.projectDetermined(projectData, selectionCount);
				return projectData;
			}

			public N findNode(S selection, int selectionCount) {
				if (currentSelectionCount.get() > selectionCount)
					return null;
				return strategy.findNode(selection, selectionCount);
			}

			public ExpressionData findExpressionData(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				ExpressionData expressionData = strategy.findExpressionData(selection, node, selectionCount);
				listenerManager.classAndMethodSelectionOccured(expressionData, selectionCount);
				return expressionData;
			}

			public FileNameAndDigest findFileAndDigest(S selection, N node, int selectionCount) {
				if (node == null || currentSelectionCount.get() > selectionCount)
					return null;
				FileNameAndDigest fileNameAndDigest = strategy.findFileAndDigest(selection, node, selectionCount);
				if (currentSelectionCount.get() > selectionCount)
					return null;
				if (fileNameAndDigest.fileName == null)
					listenerManager.notJavaElement(selectionCount);
				else {
					String digest = fileNameAndDigest.digest;
					if (digest == null)
						listenerManager.notInAJar(fileNameAndDigest, selectionCount);
					else
						listenerManager.digestDetermined(fileNameAndDigest, selectionCount);
				}
				return fileNameAndDigest;
			}
		};
		this.executor = executor;
	}

	public Future<?> selectionOccured(S selection) {
		final int thisSelectionCount = currentSelectionCount.incrementAndGet();
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
		ExpressionData expressionData = strategy.findExpressionData(selection, node, thisSelectionCount);
		return new Runnable() {
			public void run() {
				try {
					FileNameAndDigest fileNameAndDigest = strategy.findFileAndDigest(selection, node, thisSelectionCount);
					if (fileNameAndDigest != null && fileNameAndDigest.digest != null)
						strategy.findProject(selection, fileNameAndDigest, thisSelectionCount);
				} catch (RuntimeException e) {
					ICallback.Utils.call(exceptionHandler, e);
				}
			}
		};
	}

	public void addSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listenerManager.addSelectedArtifactSelectionListener(listener);
	}

	public void removeSelectedArtifactSelectionListener(ISelectedBindingListener listener) {
		listenerManager.removeSelectedArtifactSelectionListener(listener);
	}

	public void dispose() {
		listenerManager.dispose();
	}

}