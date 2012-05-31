package org.softwarefm.views;

import org.junit.Test;
import org.softwarefm.eclipse.jdtBinding.ExpressionData;

public class ProjectPanelTest extends TextAndBrowserPanelTest<ProjectView, ProjectPanel> {

	private final String initialUrl = "initialUrl";

	@Test
	public void testListenerMethodsWhenNotJavaElement() {
		panel.classAndMethodSelectionOccured(new ExpressionData("packagename", "className"), 1);
		assertEquals(panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.notJavaElement(1);
		assertEquals(panel.notJavaElementMsg(), panel.getText());
		assertEquals("", panel.getUrl());

	}

	public void testListenerMethodsWhenNotAJar() {
		panel.classAndMethodSelectionOccured(new ExpressionData("packagename", "className"), 1);
		assertEquals(panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.notInAJar(fileNameAndNoDigest, 1);
		assertEquals(panel.notInAJarMsg(fileNameAndNoDigest), panel.getText());
		assertEquals("", panel.getUrl());

	}

	public void testWhenProjectDataFound() {
		panel.classAndMethodSelectionOccured(new ExpressionData("packagename", "className"), 1);
		assertEquals(panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.digestDetermined(fileNameAndDigest, 1);
		assertEquals(panel.digestDeterminedMsg(fileNameAndDigest) + "\n" + panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.projectDetermined(projectData, 1);
		assertEquals(panel.digestDeterminedMsg(fileNameAndDigest) + "\n" + panel.projectDeterminedMsg(projectData), panel.getText());
		assertEquals(projectUrl, panel.getUrl());

	}

	public void testWhenProjectDataNotFound() {
		panel.classAndMethodSelectionOccured(new ExpressionData("packagename", "className"), 1);
		assertEquals(panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.digestDetermined(fileNameAndDigest, 1);
		assertEquals(panel.digestDeterminedMsg(fileNameAndDigest) + "\n" + panel.searchingMsg(), panel.getText());
		assertEquals(initialUrl, panel.getUrl());

		panel.unknownDigest(fileNameAndDigest, 1);
		assertEquals(panel.digestDeterminedMsg(fileNameAndDigest) + "\n" + panel.unknownDigestMsg(fileNameAndDigest), panel.getText());
		assertEquals(digestUrl, panel.getUrl());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		panel.setText("someStuff");
		panel.setUrl(initialUrl);
	}

	@Override
	protected ProjectView makeView() {
		return new ProjectView();
	}

}
