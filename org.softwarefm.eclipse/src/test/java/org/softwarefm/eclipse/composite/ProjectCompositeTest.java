package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.utilities.constants.CommonConstants;

public class ProjectCompositeTest extends AbstractSoftwareFmCompositeTest<ProjectComposite> {

	public void testMessages() {
		assertEquals(UrlConstants.welcomeUrl, panel.getTextOrUrl());// initial
		checkDisplaysInBrowser("Searching...", new Runnable() {
			public void run() {
				listenerManager.classAndMethodSelectionOccured(classExpressionData, 0);
			}
		});
		checkDisplaysInBrowser("The selected item was defined in file " + file + "\nSearching...", new Runnable() {
			public void run() {
				listenerManager.digestDetermined(fileNameAndDigest, 0);
			}
		});
		checkDisplaysInBrowser(CommonConstants.softwareFmHostAndPrefix + "/project/g/a", new Runnable() {
			public void run() {
				listenerManager.projectDetermined(projectData, 0);
			}
		});
		
		listenerManager.unknownDigest(fileNameAndDigest, 0);
		dispatchUntilQueueEmpty();
		assertEquals(panel.getSecondaryControl().getControl(), panel.getTopControl());
	}

	private void checkDisplaysInBrowser(String expected, Runnable runnable) {
		panel.showSecondaryControl();
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(expected, panel.getTextOrUrl());
		assertEquals(panel.getBrowser(), panel.getTopControl());
	}

	@Override
	protected ProjectComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ProjectComposite(parent, container);
	}

}
