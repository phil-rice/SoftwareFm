package org.softwarefm.eclipse.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.TextKeys;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;

public class SoftwareFmCompositeTest extends AbstractSoftwareFmCompositeTest<CodeComposite> {

	public void testAddListenerDelegatesToManager() {
		assertEquals(1, listenerManager.getListeners().size());
		ISelectedBindingListener listener = EasyMock.createMock(ISelectedBindingListener.class);
		EasyMock.replay(listener);

		panel.addListener(listener);
		assertEquals(2, listenerManager.getListeners().size());
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
		assertEquals("The selected item was defined in file "+ file + " which isn't a jar", panel.notInAJarMsg(file));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345", panel.digestDeterminedMsg(TextKeys.msgTestUnknownDigest, fileAndDigest));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345 ArtifactData " + artifactData + " GroupId g ArtifactId a Version v", panel.artifactDeterminedMsg(TextKeys.msgTestArtifactDetermined, artifactData));
		assertEquals("File " + file + " Digest 0123456789 Digest6 012345", panel.unknownDigestMsg(TextKeys.msgTestUnknownDigest, fileAndDigest));
	}

	@Override
	protected CodeComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new CodeComposite(parent, container);
	}

}
