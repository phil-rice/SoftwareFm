package org.softwarefm.core.selection.internal;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMock;
import org.softwarefm.core.cache.CachedArtifactData;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.selection.ISelectedBindingStrategy;
import org.softwarefm.core.tests.ExecutorTestCase;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class SelectedArtifactSelectionManagerTest extends ExecutorTestCase {

	private ISelectedBindingStrategy<String, String> strategy;
	private ISelectedBindingListener listener;
	private SelectedArtifactSelectionManager<String, String> selectionManager;
	private final CodeData codeData = new CodeData("package", "class");
	private final File file = new File(new File("some"), "file");
	private final FileAndDigest fileAndDigest = new FileAndDigest(file, "digest");
	private final ArtifactData artifactData = new ArtifactData(fileAndDigest, "g", "a", "v");
	private IArtifactDataCache artifactDataCache;

	public void testWhenSelectionIsNull() throws Exception {
		int count = 1;
		listener.notJavaElement(count);
		EasyMock.replay(listener, strategy);

		selectionManager.selectionOccured(null).get();

	}

	public void testRosyView() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listener.codeSelectionOccured(count, codeData);
		listener.digestDetermined(count, fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
		listener.artifactDetermined(count, artifactData);
		EasyMock.replay(listener, strategy);

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
		listener.codeSelectionOccured(count, codeData);
		listener.digestDetermined(count, fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
		listener.artifactDetermined(count, artifactData);
		EasyMock.replay(listener, strategy);

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
			listener.codeSelectionOccured(count, codeData);
			listener.artifactDetermined(count, artifactData);
		}
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(fileAndDigest);
		listener.digestDetermined(count, fileAndDigest);
		EasyMock.expect(strategy.findArtifact("selection", fileAndDigest, count)).andReturn(artifactData);
		EasyMock.replay(listener, strategy);

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
		listener.codeSelectionOccured(count, codeData);
		listener.notJavaElement(count);
		EasyMock.replay(listener, strategy);

		selectionManager.selectionOccured("selection").get();

		assertNull(artifactDataCache.projectData(file));
	}

	public void testIfDigestNotFoundNotAJarIsCalled() throws InterruptedException, ExecutionException {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, count)).andReturn(null);
		listener.codeSelectionOccured(count, codeData);
		listener.notInAJar(count, file);
		EasyMock.replay(listener, strategy);

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
		listener.codeSelectionOccured(count, codeData);
		listener.digestDetermined(count, fileAndDigest);
		listener.unknownDigest(count, fileAndDigest);
		EasyMock.replay(listener, strategy);

		selectionManager.selectionOccured("selection").get();

		assertEquals(CachedArtifactData.notFound(fileAndDigest), artifactDataCache.projectData(file));
	}

	public void testIfProjectDataInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listener.codeSelectionOccured(count, codeData);
		listener.artifactDetermined(count, artifactData);
		EasyMock.replay(listener, strategy);

		artifactDataCache.addProjectData(artifactData);
		selectionManager.selectionOccured("selection").get();

	}

	public void testIfNotFoundInCache() throws Exception {
		int count = 1;
		EasyMock.expect(strategy.findNode("selection", count)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", count)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", count)).andReturn(file);
		listener.codeSelectionOccured(count, codeData);
		listener.unknownDigest(count, fileAndDigest);
		EasyMock.replay(listener, strategy);

		artifactDataCache.addNotFound(fileAndDigest);
		selectionManager.selectionOccured("selection").get();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		strategy = EasyMock.createMock(ISelectedBindingStrategy.class);
		listener = EasyMock.createMock(ISelectedBindingListener.class);

		EasyMock.makeThreadSafe(listener, true);
		EasyMock.makeThreadSafe(strategy, true);
		artifactDataCache = IArtifactDataCache.Utils.artifactDataCache();
		IMultipleListenerList listenerList = IMultipleListenerList.Utils.defaultList();
		selectionManager = new SelectedArtifactSelectionManager<String, String>(listenerList, strategy, getExecutor(), artifactDataCache, ICallback.Utils.rethrow());
		selectionManager.addSelectedArtifactSelectionListener(listener);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(listener, strategy);
		super.tearDown();
	}
}
