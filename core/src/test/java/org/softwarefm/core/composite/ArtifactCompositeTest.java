package org.softwarefm.core.composite;

import org.easymock.EasyMock;
import org.eclipse.swt.widgets.Composite;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.browser.BrowserCompositeTest;
import org.softwarefm.core.constants.UrlConstants;
import org.softwarefm.shared.constants.CommonConstants;

public class ArtifactCompositeTest extends AbstractSoftwareFmCompositeTest<ArtifactComposite> {

	public void testUrlNotChangedWhenInEditingModeWithNoDigest() {
		panel.getBrowser().setUrlAndWait(BrowserCompositeTest.editingUrl1);
		String originalTextOrUrl = panel.getTextOrUrl();
		setUpMockForNoDigest();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(originalTextOrUrl, panel.getTextOrUrl());// unfortunately the browser does not have getUrl method, so this is the url we have changed to with selection
	}

	public void testUrlNotChangedWhenInEditingModeWithUnknownDigest() {
		panel.getBrowser().setUrlAndWait(BrowserCompositeTest.editingUrl1);
		String originalTextOrUrl = panel.getTextOrUrl();
		setUpMockForUnknownDigest();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(originalTextOrUrl, panel.getTextOrUrl());// unfortunately the browser does not have getUrl method, so this is the url we have changed to with selection
	}

	public void testUrlNotChangedWhenInEditingModeWithFoundArtifact() {
		panel.getBrowser().setUrlAndWait(BrowserCompositeTest.editingUrl1);
		String originalTextOrUrl = panel.getTextOrUrl();
		setupMockForFoundArtifact();
		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals(originalTextOrUrl, panel.getTextOrUrl());// unfortunately the browser does not have getUrl method, so this is the url we have changed to with selection
	}

	public void testUnknownDigestCausesLinkCompositeToAppear() throws Exception {
		setUpMockForUnknownDigest();

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		LinkComposite linkComposite = panel.getSecondaryControl();
		assertEquals(linkComposite.getControl(), panel.getTopControl());
		assertTrue(linkComposite.getText(), linkComposite.getText().contains("File: some\\file"));
		assertTrue(linkComposite.getText(), linkComposite.getText().contains("Digest: 0123456789"));
	}

	public void testArtifactSelectionCausesBrowserToViewArtifactUrl() throws Exception {
		setupMockForFoundArtifact();

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals("http://www.softwarefm.org/mediawiki/index.php/artifact:groupId0/artifactId0", panel.getTextOrUrl());
	}

	public void testNoDigestCausesBrowserToNotAJar() throws Exception {
		setUpMockForNoDigest();

		execute(new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.selectionOccured("selection");
			}
		});
		EasyMock.verify(strategy);
		assertEquals("http://www.softwarefm.org/mediawiki/index.php/notJar", panel.getTextOrUrl());
	}

	public void testMessages() {
		assertEquals(UrlConstants.aboutArtifactComposite, panel.getTextOrUrl());// initial
		selectedArtifactSelectionManager.codeSelectionOccured(0, classData);
		assertEquals(UrlConstants.aboutArtifactComposite, panel.getTextOrUrl());// initial
		assertEquals(panel.getBrowser().getControl(), panel.getTopControl());

		checkDisplaysInBrowser("The selected item was defined in file " + file + "\nSearching...", new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.digestDetermined(0, fileAndDigest);
			}
		});
		checkDisplaysInBrowser("http://"+CommonConstants.softwareFmHostAndPrefix + "/artifact:groupId0/artifactId0", new Runnable() {
			public void run() {
				selectedArtifactSelectionManager.artifactDetermined(0, artifactData);
			}
		});

		selectedArtifactSelectionManager.unknownDigest(0, fileAndDigest);
		dispatchUntilQueueEmpty();
		assertEquals(panel.getSecondaryControl().getControl(), panel.getTopControl());
	}

	private void checkDisplaysInBrowser(String expected, Runnable runnable) {
		panel.showSecondaryControl();
		runnable.run();
		dispatchUntilQueueEmpty();
		assertEquals(expected, panel.getTextOrUrl());
		assertEquals(panel.getBrowser().getControl(), panel.getTopControl());
	}

	@Override
	protected ArtifactComposite makePanel(Composite parent, SoftwareFmContainer<?> container) {
		return new ArtifactComposite(parent, container);
	}

}
