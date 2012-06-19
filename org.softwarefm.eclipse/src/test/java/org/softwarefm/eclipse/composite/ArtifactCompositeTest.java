package org.softwarefm.eclipse.composite;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.eclipse.SoftwareFmContainer;
import org.softwarefm.eclipse.constants.UrlConstants;
import org.softwarefm.utilities.constants.CommonConstants;

public class ArtifactCompositeTest extends AbstractSoftwareFmCompositeTest<ArtifactComposite> {

	public void testMessages() {
		assertEquals(UrlConstants.aboutArtifactComposite, panel.getTextOrUrl());// initial
		listenerManager.codeSelectionOccured(classExpressionData, 0);
		assertEquals(UrlConstants.aboutArtifactComposite, panel.getTextOrUrl());// initial
		assertEquals(panel.getBrowser(), panel.getTopControl());

		checkDisplaysInBrowser("The selected item was defined in file " + file + "\nSearching...", new Runnable() {
			public void run() {
				listenerManager.digestDetermined(fileAndDigest, 0);
			}
		});
		checkDisplaysInBrowser(CommonConstants.softwareFmHostAndPrefix + "/artifact:g/a", new Runnable() {
			public void run() {
				listenerManager.artifactDetermined(artifactData, 0);
			}
		});

		listenerManager.unknownDigest(fileAndDigest, 0);
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
	protected ArtifactComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ArtifactComposite(parent, container);
	}

}
