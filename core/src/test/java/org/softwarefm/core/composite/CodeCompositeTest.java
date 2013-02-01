package org.softwarefm.core.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.browser.BrowserCompositeTest;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.selection.ISelectedBindingListener;

public class CodeCompositeTest extends AbstractSoftwareFmCompositeTest<CodeComposite> {

	public void testClassAndMethodSelectionCausesBrowserToViewUrl() throws Exception {
		setUpMockForUnknownDigest();

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals("http://"+classAndMethodNameUrl, panel.getUrl());
	}

	public void testUrlNotChangedWhenInEditingMode() {
		panel.getBrowserForTest().setUrlAndWait(BrowserCompositeTest.editingUrl1);
		setupMockForFoundArtifact();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(null, panel.getUrl());// unfortunately the browser does not have getUrl method, so this is the url we have changed to with selection
	}

	public void testListenersAreNotRegisteredWhenDisposed() {
		setupMockForFoundArtifact();
		panel.dispose();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
	}

	public void testAddListenerDelegatesToManager() {
		assertEquals(2, selectedArtifactSelectionManager.getListeners().size());
		ISelectedBindingListener listener = EasyMock.createMock(ISelectedBindingListener.class);
		EasyMock.replay(listener);

		panel.addSelectedBindingListener(listener);
		assertEquals(3, selectedArtifactSelectionManager.getListeners().size());
		assertTrue(selectedArtifactSelectionManager.getListeners().contains(listener));
		EasyMock.verify(listener);
	}

	public void testDigestUrl() {
		assertEquals(digestUrl, panel.digestUrl(fileAndDigest));
	}

	public void testMsgs() {
		assertEquals("1and2", panel.msg(TextKeys.msgTestForTest, 1, 2));
		assertEquals("Searching...", panel.searchingMsg());
		assertEquals("SoftwareFM was unable to work out what sort of JavaElement the selected item was", panel.notJavaElementMsg());
		assertEquals("The selected item was defined in file " + file + " which isn't a jar", panel.notInAJarMsg(file));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345", panel.digestDeterminedMsg(TextKeys.msgTestUnknownDigest, fileAndDigest));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345 ArtifactData " + artifactData + " GroupId groupId0 ArtifactId artifactId0 Version version0", panel.artifactDeterminedMsg(TextKeys.msgTestArtifactDetermined, artifactData));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345", panel.unknownDigestMsg(TextKeys.msgTestUnknownDigest, fileAndDigest));
	}

	@Override
	protected CodeComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}
}
