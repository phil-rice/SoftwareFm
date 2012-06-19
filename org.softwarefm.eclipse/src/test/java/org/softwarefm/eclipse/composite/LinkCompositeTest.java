package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabItem;
import org.softwarefm.eclipse.SoftwareFmContainer;

public class LinkCompositeTest extends AbstractSoftwareFmCompositeTest<LinkComposite> {

	public void testMessages() {
		assertEquals("", panel.getText());// initial
		listenerManager.codeSelectionOccured(classExpressionData, 0);
		dispatchUntilQueueEmpty();
		assertEquals("Searching...", panel.getText());

		listenerManager.digestDetermined(fileAndDigest, 0);
		dispatchUntilQueueEmpty();
		assertEquals("Searching...", panel.getText());

		listenerManager.artifactDetermined(artifactData, 0);
		dispatchUntilQueueEmpty();
		assertEquals("File: " + fileAndDigest.file + "\nDigest " + fileAndDigest.digest + "\nIf you think that the jar has been linked to the wrong project, you can edit it below", panel.getText());

		listenerManager.unknownDigest(fileAndDigest, 0);
		dispatchUntilQueueEmpty();
		assertEquals("File: " + fileAndDigest.file + "\nDigest: " + fileAndDigest.digest + "\n" + //
				"This has not been added to SoftwareFm\n" + //
				"In order to link this jar to SoftwareFM, we would like you to tell use some information about it. \n" + //
				"Ideally you will give us the url of the Maven POM that created the jar, but can you can manually enter the relevant data if you want.", panel.getText());
	}

	public void testEventsChangeSelectedTab() {
		checkDoesntChangeTab(new Runnable() {
			public void run() {
				listenerManager.codeSelectionOccured(classExpressionData, 0);
			}
		});
		checkDoesntChangeTab(new Runnable() {
			public void run() {
				listenerManager.notInAJar(file, 0);
			}
		});
		checkDoesntChangeTab(new Runnable() {
			public void run() {
				listenerManager.notJavaElement( 0);
			}
		});
		checkDoesntChangeTab(new Runnable() {
			public void run() {
				listenerManager.digestDetermined(fileAndDigest,  0);
			}
		});
		checkChangesTabTo(panel.manualImportTabItem, new Runnable() {
			public void run() {
				listenerManager.artifactDetermined(artifactData,  0);
			}
		});
		checkChangesTabTo(panel.mavenImportTabItem, new Runnable() {
			public void run() {
				listenerManager.unknownDigest(fileAndDigest, 0);
			}
		});
	}

	private void checkChangesTabTo(TabItem tabItem, Runnable runnable) {
		panel.tabFolder.setSelection(panel.manualImportTabItem);
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(tabItem, panel.tabFolder.getSelection()[0]);
		
		panel.tabFolder.setSelection(panel.mavenImportTabItem);
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(tabItem, panel.tabFolder.getSelection()[0]);
		
	}

	private void checkDoesntChangeTab(Runnable runnable) {
		panel.tabFolder.setSelection(panel.manualImportTabItem);
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(panel.manualImportTabItem, panel.tabFolder.getSelection()[0]);

		panel.tabFolder.setSelection(panel.mavenImportTabItem);
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(panel.mavenImportTabItem, panel.tabFolder.getSelection()[0]);
	}

	@Override
	protected LinkComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new LinkComposite(parent, container);
	}

}
