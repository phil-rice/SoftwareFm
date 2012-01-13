package org.softwareFm.collections.explorer.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Urls;

public class ExplorerUnrecognisedJarIntegrationTest extends AbstractExplorerIntegrationTest {
	private final File rtFile = new File("a/b/c/jdk1.2.3/rt.jar");

	public void testRtJar() {
		explorer.displayUnrecognisedJar(rtFile, "012345", "someProject");
		Control originalMessage = masterDetailSocial.getMasterContent();
		StyledText originalText = (StyledText) Swts.getDescendant(originalMessage, 1, 0, 0);
		assertTrue(originalText.getText().contains("This is a version of the Java runtime that"));
		originalText.notifyListeners(SWT.MouseUp, new Event());
		Control importingMessage = masterDetailSocial.getMasterContent();
		// possible race condition here...the card may arrive before we have tested that the import text occured. I think that because we are not dispatching this won't take place
		Swts.layoutDump(importingMessage);
		StyledText importingText = (StyledText) Swts.getDescendant(importingMessage, 1, 0, 0);
		System.out.println(importingText.getText());
		dispatchUntil(display, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				ICardHolder cardHolder = explorer.getCardHolder();
				ICard card = cardHolder.getCard();
				return card != null;
			}
		});
		ICardHolder cardHolder = explorer.getCardHolder();
		ICard card = cardHolder.getCard();
		assertEquals(Urls.composeWithSlash(rootArtifactUrl, "sun/jdk/sun.jdk/artifact/runtime"), card.url());

		checkRepositoryPopulated("012345", "sun.jdk", "runtime", "1.2.3");
	}

	private void checkRepositoryPopulated(String digest, String group, String artifact, String version) {
		Map<String, Object> groupArtifactVersion = Maps.stringObjectLinkedMap(CollectionConstants.groupId, group, CollectionConstants.artifactId, artifact, CardConstants.version, version);
		Map<String, Object> groupArtifactVersionDigest = Maps.with(groupArtifactVersion, CardConstants.digest, digest);

		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
		String jarUrl = jarUrlGenerator.findUrlFor(groupArtifactVersionDigest);
		assertEquals(groupArtifactVersion, Json.mapFromString(Files.getText(new File(localRoot, Urls.compose(jarUrl, ServerConstants.dataFileName)))));
		assertEquals(groupArtifactVersion, Json.mapFromString(Files.getText(new File(remoteRoot, Urls.compose(jarUrl, ServerConstants.dataFileName)))));

		IUrlGenerator artifactGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
		String artifactUrl = artifactGenerator.findUrlFor(groupArtifactVersion);
		assertEquals(Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact), Json.mapFromString(Files.getText(new File(remoteRoot, Urls.compose(artifactUrl, ServerConstants.dataFileName)))));
		
	
	}

}
