package org.softwarefm.core.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.constants.TextKeys;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.ISelectedBindingListener;

public class CodeCompositeTest extends AbstractSoftwareFmCompositeTest<CodeComposite> {
	private final CodeData codeData = new CodeData("packageName", "className");

	public void testClassAndMethodSelectionCausesBrowserToViewUrl() throws Exception {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(fileNameAndNoDigest);
		EasyMock.replay(strategy);

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(classAndMethodNameUrl, panel.getUrl());
	}

	public void testListenersAreNotRegisteredWhenDisposed() {
		EasyMock.expect(strategy.findNode("selection", 1)).andReturn("node");
		EasyMock.expect(strategy.findExpressionData("selection", "node", 1)).andReturn(codeData);
		EasyMock.expect(strategy.findFile("selection", "node", 1)).andReturn(file);
		EasyMock.expect(strategy.findDigest("selection", "node", file, 1)).andReturn(fileNameAndNoDigest);
		EasyMock.replay(strategy);
		panel.dispose();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
	}

	public void testAddListenerDelegatesToManager() {
		assertEquals(2, listenerManager.getListeners().size());
		ISelectedBindingListener listener = EasyMock.createMock(ISelectedBindingListener.class);
		EasyMock.replay(listener);

		panel.addListener(listener);
		assertEquals(3, listenerManager.getListeners().size());
		assertTrue(listenerManager.getListeners().contains(listener));
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
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345 ArtifactData " + artifactData + " GroupId g ArtifactId a Version v", panel.artifactDeterminedMsg(TextKeys.msgTestArtifactDetermined, artifactData));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345", panel.unknownDigestMsg(TextKeys.msgTestUnknownDigest, fileAndDigest));
	}

	@Override
	protected CodeComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}
}
