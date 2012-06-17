package org.softwarefm.eclipse.selection.internal;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.cache.CachedProjectData;
import org.softwarefm.eclipse.cache.IProjectDataCache;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;
import org.softwarefm.eclipse.selection.ISelectedBindingListenerAndAdderRemover;
import org.softwarefm.eclipse.selection.ISelectedBindingStrategy;
import org.softwarefm.eclipse.tests.ExecutorTestCase;
import org.softwarefm.utilities.callbacks.ICallback;

public class SelectedArtifactSelectionManagerTest extends ExecutorTestCase {

	private ISelectedBindingListenerAndAdderRemover<String> listenerManager;
	private ISelectedBindingStrategy<String, String> strategy;
	private SelectedArtifactSelectionManager<String, String> selectionManager;
	private final ExpressionData expressionData = new ExpressionData("package", "class");
	private final File file = new File(new File("some"), "file");
	private final FileAndDigest fileAndDigest = new FileAndDigest(file, "digest");
	private final ProjectData projectData = new ProjectData(fileAndDigest, "g", "a", "v");
	private IProjectDataCache projectDataCache;

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
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findProject("selection", fileAndDigest, count)).andReturn(projectData);
		listenerManager.projectDetermined(projectData, count);
		EasyMock.replay(listenerManager, strategy);

		assertEquals(0, selectionManager.currentSelectionId());
		selectionManager.selectionOccured("selection").get();
		assertEquals(1, selectionManager.currentSelectionId());

		assertEquals(CachedProjectData.found(projectData), projectDataCache.projectData(file));
	}

	public void testReselectIgnoresIfIdNotEqualToCurrentSelection() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findProject("selection", fileAndDigest, count)).andReturn(projectData);
		listenerManager.projectDetermined(projectData, count);
		EasyMock.replay(listenerManager, strategy);

		assertEquals(0, selectionManager.currentSelectionId());
		selectionManager.selectionOccured("selection").get();
		assertEquals(1, selectionManager.currentSelectionId());

		selectionManager.reselect(0);
		selectionManager.reselect(2);

	}

	public void testReselectsIfIdEqualToCurrentSelection() throws Exception {
		int count = 1;
		for (int i = 0; i < 3; i++) {
			EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
			EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
			EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
			listenerManager.classAndMethodSelectionOccured(expressionData, count);
			listenerManager.projectDetermined(projectData, count);
		}
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findProject("selection", fileAndDigest, count)).andReturn(projectData);
		EasyMock.replay(listenerManager, strategy);

		assertEquals(0, selectionManager.currentSelectionId());
		selectionManager.selectionOccured("selection").get();
		assertEquals(1, selectionManager.currentSelectionId());
		selectionManager.reselect(1);
		selectionManager.reselect(1);
		assertEquals(1, selectionManager.currentSelectionId());

	}

	public void testIfFileNotFound() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(null);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.notJavaElement(count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertNull(projectDataCache.projectData(file));
	}

	public void testIfDigestNotFoundNotAJarIsCalled() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(null);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.notInAJar(file, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertNull(projectDataCache.projectData(file));
	}

	public void testIfCannotFindDigest() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		EasyMock.expect(strategy.findProject("selection", fileAndDigest, count)).andReturn(null);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		listenerManager.unknownDigest(fileAndDigest, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertEquals(CachedProjectData.notFound(fileAndDigest), projectDataCache.projectData(file));
	}

	public void testIfProjectDataInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.projectDetermined(projectData, count);
		EasyMock.replay(listenerManager, strategy);

		projectDataCache.addProjectData(projectData);
		selectionManager.selectionOccured("selection").get();

	}

	public void testIfNotFoundInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(expressionData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listenerManager.classAndMethodSelectionOccured(expressionData, count);
		listenerManager.unknownDigest(fileAndDigest, count);
		EasyMock.replay(listenerManager, strategy);

		projectDataCache.addNotFound(fileAndDigest);
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
		projectDataCache = IProjectDataCache.Utils.projectDataCache();
		selectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), projectDataCache, ICallback.Utils.rethrow());
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(listenerManager, strategy);
		super.tearDown();
	}
}
