package org.softwarefm.views;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.selection.ISelectedBindingListener;

public class SoftwareFmPanelTest extends AbstractSoftwareFmPanelTest<ClassAndMethodView, ClassAndMethodPanel> {

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
		assertEquals("localhost/wiki/digest/0123456789", panel.digestUrl(fileNameAndDigest));
	}

	public void testMsgs() {
		assertEquals("1and2", panel.msg("message.fortest.text", 1, 2));
		assertEquals("Searching...", panel.searchingMsg());
		assertEquals("The selected item was defined in file fileName", panel.digestDeterminedMsg(fileNameAndDigest));
		assertEquals("The selected item was defined in file fileName which isn't a jar", panel.notInAJarMsg(fileNameAndNoDigest));
		assertEquals("Found Group Id g Artefact Id a Version Id v", panel.projectDeterminedMsg(projectData));
		assertEquals("File fileName has not been added to SoftwareFm\nTo add it follow <these instructions>", panel.unknownDigestMsg(fileNameAndDigest));
		assertEquals("SoftwareFM was unable to work out what sort of JavaElement the selected item was", panel.notJavaElementMsg());
	}

	@Override
	protected ClassAndMethodView makeView() {
		return new ClassAndMethodView();
	}

}
