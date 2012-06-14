package org.softwarefm.eclipse.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;

public class SoftwareFmCompositeTest extends AbstractSoftwareFmCompositeTest<ClassAndMethodComposite> {

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
		assertEquals(digestUrl, panel.digestUrl(fileNameAndDigest));
	}

	public void testMsgs() {
		assertEquals("1and2", panel.msg("message.fortest.text", 1, 2));
		assertEquals("Searching...", panel.searchingMsg());
		assertEquals("The selected item was defined in file " + file, panel.digestDeterminedMsg(fileNameAndDigest));
		assertEquals("The selected item was defined in file " + file + " which isn't a jar", panel.notInAJarMsg(fileNameAndNoDigest));
		assertEquals("Found Group Id [g] Artifact Id [a] Version [v]", panel.projectDeterminedMsg(projectData));
		assertEquals("File: " + file + "\n" + //
				"with digest 0123456789 has not been added to SoftwareFm\n" + //
				"Please try and add information about it", panel.unknownDigestMsg(fileNameAndDigest));
		assertEquals("SoftwareFM was unable to work out what sort of JavaElement the selected item was", panel.notJavaElementMsg());
	}

	@Override
	protected ClassAndMethodComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ClassAndMethodComposite(parent, container);
	}

}
