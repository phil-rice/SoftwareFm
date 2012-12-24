package org.softwarefm.eclipse.selection.internal;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.cache.CachedArtifactData;
import org.softwarefm.eclipse.cache.IArtifactDataCache;
import org.softwarefm.eclipse.jdtBinding.CodeData;
import org.softwarefm.eclipse.jdtBinding.ArtifactData;
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
	private final CodeData codeData = new CodeData("package", "class");
	private final File file = new File(new File("some"), "file");
	private final FileAndDigest fileAndDigest = new FileAndDigest(file, "digest");
	private final ArtifactData artifactData = new ArtifactData(fileAndDigest, "g", "a", "v");
	private IArtifactDataCache artifactDataCache;

	public void testWhenSelectionIsNull() throws Exception {
		int count = 1;
		listenerManager.notJavaElement(count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured(null).get();

	}

	public void testRosyView() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
		listenerManager.artifactDetermined(artifactData, count);
		EasyMock.replay(listenerManager, strategy);

		assertEquals(0, selectionManager.currentSelectionId());
		selectionManager.selectionOccured("selection").get();
		assertEquals(1, selectionManager.currentSelectionId());

		assertEquals(CachedArtifactData.found(artifactData), artifactDataCache.projectData(file));
	}

	public void testReselectIgnoresIfIdNotEqualToCurrentSelection() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
		listenerManager.artifactDetermined(artifactData, count);
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
			EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
			EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
			listenerManager.codeSelectionOccured(codeData, count);
			listenerManager.artifactDetermined(artifactData, count);
		}
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listenerManager.digestDetermined(fileAndDigest, count);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
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
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(null);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.notJavaElement(count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertNull(artifactDataCache.projectData(file));
	}

	public void testIfDigestNotFoundNotAJarIsCalled() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(null);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.notInAJar(file, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertNull(artifactDataCache.projectData(file));
	}

	public void testIfCannotFindDigest() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(null);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.digestDetermined(fileAndDigest, count);
		listenerManager.unknownDigest(fileAndDigest, count);
		EasyMock.replay(listenerManager, strategy);

		selectionManager.selectionOccured("selection").get();

		assertEquals(CachedArtifactData.notFound(fileAndDigest), artifactDataCache.projectData(file));
	}

	public void testIfProjectDataInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.artifactDetermined(artifactData, count);
		EasyMock.replay(listenerManager, strategy);

		artifactDataCache.addProjectData(artifactData);
		selectionManager.selectionOccured("selection").get();

	}

	public void testIfNotFoundInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listenerManager.codeSelectionOccured(codeData, count);
		listenerManager.unknownDigest(fileAndDigest, count);
		EasyMock.replay(listenerManager, strategy);

		artifactDataCache.addNotFound(fileAndDigest);
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
		artifactDataCache = IArtifactDataCache.Utils.artifactDataCache();
		selectionManager = new SelectedArtifactSelectionManager<String, String>(listenerManager, strategy, getExecutor(), artifactDataCache, ICallback.Utils.rethrow());
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(listenerManager, strategy);
		super.tearDown();
	}
}