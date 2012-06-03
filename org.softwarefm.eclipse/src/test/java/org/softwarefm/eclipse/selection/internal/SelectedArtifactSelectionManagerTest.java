package org.softwarefm.eclipse.selection.internal;

import java.util.concurrent.ExecutionException;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.ExecutorTestCase;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.utilities.callbacks.ICallback;

public class SelectedArtifactSelectionManagerTest extends ExecutorTestCase {

	private ISelectedBindingListenerAndAdderRemover<String> listenerManager;
	private ISelectedBindingStrategy<String, String> strategy;
	private SelectedArtifactSelectionManager<String, String> selectionManager;
	private final ExpressionData expressionData = new ExpressionData("package", "class");
	private final FileNameAndDigest fileNameAndDigest = new FileNameAndDigest("fileName", "digest");
	private final FileNameAndDigest noFileNameAndNoDigest = new FileNameAndDigest(null, null);
	private final FileNameAndDigest fileNameAndNoDigest = new FileNameAndDigest("fileName", null);
	private final ProjectData projectData = new ProjectData(fileNameAndDigest, "g", "a", "v");

	public void testWhenSelectionIsNull() throws Exception {
		int count = 1;
		listenerManager.notJavaElement(count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured(null).get();

	}

	public void testRosyView() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", count)).andReturn(fileNameAndDigest);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.digestDetermined(fileNameAndDigest, count);
		EasyMock.expect(strategy.findProject(fileNameAndDigest, count)).andReturn(projectData);
		listenerManager.projectDetermined(projectData, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();
	}

	public void testIfFileNotFound() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", count)).andReturn(noFileNameAndNoDigest);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.notJavaElement(count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

	}

	public void testIfDigestNotFoundNotAJarIsCalled() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", count)).andReturn(fileNameAndNoDigest);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.notInAJar(fileNameAndNoDigest, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();
	}

	public void testIfCannotFindDigest() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFileAndDigest("selection", "node", count)).andReturn(fileNameAndDigest);
		EasyMock.expect(strategy.findProject(fileNameAndDigest, count)).andReturn(null);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.digestDetermined(fileNameAndDigest, count);
		listenerManager.unknownDigest(fileNameAndDigest, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();
	}

	public void testDelegatesAddListener() {
		ISelectedBindingListener listener = EasyMock.createMock(ISelectedBindingListener.class);
		listenerManager.addSelectedArtifactSelectionListener(listener);
		EasyMock.replay(listenerManager, strategy);
		selectionManager.addSelectedArtifactSelectionListener(listener);
	}

	public void testDelegatesRemoveListener() {
		ISelectedBindingListener listener = EasyMock.createMock(ISelectedBindingListener.class);
		listenerManager.removeSelectedArtifactSelectionListener(listener);
		EasyMock.replay(listenerManager, strategy);
		selectionManager.removeSelectedArtifactSelectionListener(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		listenerManager = EasyMock.createMock(ISelectedBindingListenerAndAdderRemover.class);
		strategy = EasyMock.createMock(ISelectedBindingStrategy.class);
		EasyMock.makeThreadSafe(listenerManager, true);
		EasyMock.makeThreadSafe(strategy, true);
		selectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), ICallback.Utils.rethrow());
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(listenerManager, strategy);
		super.tearDown();
	}
}
