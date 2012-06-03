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
		assertEquals("localhost/wiki/digest/0123456789", panel.digestUrl(fileNameAndDigest));
	}

	public void testMsgs() {
		assertEquals("1and2", panel.msg("message.fortest.text", 1, 2));
		assertEquals("Searching...", panel.searchingMsg());
		assertEquals("The selected item was defined in file fileName", panel.digestDeterminedMsg(fileNameAndDigest));
		assertEquals("The selected item was defined in file fileName which isn't a jar", panel.notInAJarMsg(fileNameAndNoDigest));
		assertEquals("Found Group Id [g] Artifact Id [a] Version [v]", panel.projectDeterminedMsg(projectData));
		assertEquals("File fileName has not been added to SoftwareFm\n" + //
				"To add it:\n" + //
				"* Work out the GroupId, ArtifactId and Version for this jar\n" + //
				"** If the jar has a Maven POM, these are the same meanings used in the POM\n" + //
				"** If the jar is rt.jar (or on a Mac classes.jar), these are: sun.jdk, runtime, <your version>\n" + //
				"** If the jar doesnt have a POM, use the url of the project website as the GroupId, and the stem of the jar as the ArtifactId\n" + //
				"* Click the words \"edit this page\" below\n" + //
				"* Add the GroupId, ArtifactId and Version on consecutive lines, without leading or trailing spaces, or any other text\n" + //
				"* Click the save button (use the scroll bar to find it) or Alt-S", panel.unknownDigestMsg(fileNameAndDigest));
		assertEquals("SoftwareFM was unable to work out what sort of JavaElement the selected item was", panel.notJavaElementMsg());
	}

	@Override
	protected ClassAndMethodComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ClassAndMethodComposite(parent, container);
	}

}
