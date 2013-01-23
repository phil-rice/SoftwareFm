package org.softwarefm.core.selection.internal;

import java.io.File;

import org.easymock.EasyMock;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingListener;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class SwtThreadSelectedBindingAggregatorTest extends SwtTest {

	private SwtThreadSelectedBindingAggregator<String> swtThreadSelectedBindingAggregator;
	private ISelectedBindingListener listener1;
	private ISelectedBindingListener listener2;
	private final CodeData codeData = new CodeData("package", "class");
	private final File file = new File("filename");
	private final FileAndDigest fileAndDigest = new FileAndDigest(file, "digest");
	private final ArtifactData artifactData = new ArtifactData(fileAndDigest, "g", "a", "v");

	// Note that the listeners are not thread safe mocks, so their calls will be in the same thread or an exception will occur
	public void testClassAndMethodSelectionCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.codeSelectionOccured(codeData, 1);
			}
		});
	}

	public void testNotJavaElementCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.notJavaElement(1);
			}
		});

	}

	public void testDigestDeterminedCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.digestDetermined(fileAndDigest, 1);
			}
		});
	}

	public void testNotInAJarCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {

			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.notInAJar(file, 1);
			}
		});
	}

	public void testProjectDeterminedCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.artifactDetermined(artifactData, 1);
			}
		});
	}

	public void testUnknownDigestCalledInSwtThread() throws Exception {
		listenersAreValid();
		checkCalledInSameThread(new ICallback<ISelectedBindingListener>() {
			@Override
			public void process(ISelectedBindingListener listener) throws Exception {
				listener.unknownDigest(fileAndDigest, 1);
			}
		});
	}

	public void testDisposeClearsListeners() throws Exception {
		EasyMock.replay(listener1, listener2);
		swtThreadSelectedBindingAggregator.dispose();
		execute(new Runnable() {
			@Override
			public void run() {
				swtThreadSelectedBindingAggregator.unknownDigest(fileAndDigest, 1);
			}
		});
	}

	public void testRemovesIfInvalid() {
		EasyMock.expect(listener1.isValid()).andReturn(true);
		EasyMock.expect(listener2.isValid()).andReturn(false);
		listener1.unknownDigest(fileAndDigest, 1);
		EasyMock.replay(listener1, listener2);

		execute(new Runnable() {
			@Override
			public void run() {
				swtThreadSelectedBindingAggregator.unknownDigest(fileAndDigest, 1);
			}
		});
		assertEquals(1, swtThreadSelectedBindingAggregator.getListeners().size());
		assertTrue(swtThreadSelectedBindingAggregator.getListeners().contains(listener1));
	}

	public void testRemoveListener() throws Exception {
		EasyMock.expect(listener2.isValid()).andReturn(true);
		listener2.unknownDigest(fileAndDigest, 1);
		EasyMock.replay(listener1, listener2);
		swtThreadSelectedBindingAggregator.removeSelectedArtifactSelectionListener(listener1);
		execute(new Runnable() {
			@Override
			public void run() {
				swtThreadSelectedBindingAggregator.unknownDigest(fileAndDigest, 1);
			}
		});
	}

	private void checkCalledInSameThread(final ICallback<ISelectedBindingListener> callback) throws Exception {
		callback.process(listener1);
		callback.process(listener2);
		EasyMock.replay(listener1, listener2);
		execute(new Runnable() {
			@Override
			public void run() {
				ICallback.Utils.call(callback, swtThreadSelectedBindingAggregator);
			}
		});
	}

	private void listenersAreValid() {
		EasyMock.expect(listener1.isValid()).andReturn(true);
		EasyMock.expect(listener2.isValid()).andReturn(true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		swtThreadSelectedBindingAggregator = new SwtThreadSelectedBindingAggregator<String>(display, IMultipleListenerList.Utils.defaultList());
		listener1 = EasyMock.createMock(ISelectedBindingListener.class);
		listener2 = EasyMock.createMock(ISelectedBindingListener.class);
		swtThreadSelectedBindingAggregator.addSelectedArtifactSelectionListener(listener1);
		swtThreadSelectedBindingAggregator.addSelectedArtifactSelectionListener(listener2);
	}

	@Override
	protected void tearDown() throws Exception {
		dispatchUntilJobsFinished();
		EasyMock.verify(listener1, listener2);
		super.tearDown();
	}

}
