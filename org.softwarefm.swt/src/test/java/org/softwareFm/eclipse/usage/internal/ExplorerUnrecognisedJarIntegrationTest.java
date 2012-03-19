/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.editors.IDataCompositeWithFooter;
import org.softwareFm.swt.okCancel.IOkCancel;
import org.softwareFm.swt.swt.Swts;

public class ExplorerUnrecognisedJarIntegrationTest extends AbstractExplorerIntegrationTest {
	private final File rtFile = new File("a/b/c/jdk1.2.3/rt.jar");
	private final File antFile = new File("a/b/c/ant-1.2.3.jar");

	@SuppressWarnings("unchecked")
	public void testNoneRtJarWithNoHitsInJarName() {
		checkNonRtNoticeAppearsAndClickPanel();
		String text = findSearchText();
		assertTrue(text, text.contains("Searching SoftwareFM Database for other Jars that look like this"));
		// possible race condition here...the card may arrive before we have tested that the search text occured. I think that because we are not dispatching this won't take place

		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				String text = findSearchText();
				return text.contains("SoftwareFM has not seen");
			}
		});
		
		IDataCompositeWithFooter<Table, IOkCancel> detailContent = (IDataCompositeWithFooter<Table, IOkCancel>) masterDetailSocial.getDetailContent();

		Table table = detailContent.getEditor(); 
		checkAndSet(table, 0, "Group Id", "Please specify the group id", "some.group");
		checkAndSet(table, 1, "Artifact Id", "ant", "someArtifact");
		checkAndSet(table, 2, "Version", "1.2.3", "someVersion");
		clickOk(detailContent);
		dispatchUntilHasImportingMessage();
		dispatchUntilHasCardInHolder();
		ICardHolder cardHolder = explorer.getCardHolder();
		ICard card = cardHolder.getCard();
		assertEquals(Urls.composeWithSlash(rootArtifactUrl, "some/group/some.group/artifact/someArtifact"), card.url());

		checkRuntimeJarPopulated();
		checkCanPullJarData();

		checkRepositoryPopulated("012345", "some.group", "someArtifact", "someVersion");
		checkCanPullRepositoryData();

		checkCanPullJarNameData();
	}

	public void testRtJar() {
		explorer.displayUnrecognisedJar(rtFile, "012345", "someProject");
		Control originalMessage = masterDetailSocial.getMasterContent();
		StyledText originalText = Swts.<StyledText>getDescendant(originalMessage, 1, 0, 0);
		assertTrue(originalText.getText().contains("This is a version of the Java runtime that"));
		originalText.notifyListeners(SWT.MouseUp, new Event());
		Control importingMessage = masterDetailSocial.getMasterContent();
		// possible race condition here...the card may arrive before we have tested that the import text occured. I think that because we are not dispatching this won't take place
		StyledText importingText = Swts.<StyledText>getDescendant(importingMessage, 1, 0, 0);
		assertTrue(importingText.getText().startsWith("Importing"));
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
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

	private void clickOk(IDataCompositeWithFooter<Table, IOkCancel>  detailContent) {
		final Control okButton = detailContent.getFooter().okButton(); 
		Swts.Buttons.press(okButton);
	}

	private void dispatchUntilHasImportingMessage() {
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				// this is race condition territory. We may get a importing message (normally takes a while to import), then the card will appear
				Control masterContent = masterDetailSocial.getMasterContent();
				try {
					StyledText text = Swts.<StyledText>getDescendant(masterContent, 1, 0, 0);
					return text.getText().startsWith("Importing");
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	private void dispatchUntilHasCardInHolder() {
		dispatchUntil(display, CommonConstants.testTimeOutMs, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				ICardHolder cardHolder = explorer.getCardHolder();
				ICard card = cardHolder.getCard();
				return card != null;
			}
		});
	}

	private void checkAndSet(Table table, int index, String name, String existing, String newValue) {
		TableItem item = table.getItem(index);
		assertEquals(name, item.getText(0));
		assertEquals(existing, item.getText(1));
		table.select(index);
		table.notifyListeners(SWT.Selection, new Event());
		Text text = Swts.findChildrenWithClass(table, Text.class).get(index);
		assertEquals(existing, text.getText());
		text.setText(newValue);
	}

	private String findSearchText() {
		Control searchingMessage = masterDetailSocial.getMasterContent();
		// Swts.layoutDump(searchingMessage);
		StyledText importingText = Swts.<StyledText>getDescendant(searchingMessage, 1, 1);
		String text = importingText.getText();
		// System.out.println(text);
		return text;
	}

	private void checkNonRtNoticeAppearsAndClickPanel() {
		explorer.displayUnrecognisedJar(antFile, "012345", "someProject");
		Control originalMessage = masterDetailSocial.getMasterContent();
		StyledText originalText = Swts.<StyledText>getDescendant(originalMessage, 1, 0, 0);
		System.out.println(originalText.getText());
		assertTrue(originalText.getText(), originalText.getText().contains("Click this panel to add it to SoftwareFm"));
		assertTrue(originalText.getText(), !originalText.getText().contains("This is a version of the Java runtime that"));
		originalText.notifyListeners(SWT.MouseUp, new Event());
	}

	private void checkCanPullJarData() {
		checkCanPullData(CardConstants.jarUrlKey, //
				Maps.stringObjectMap(JarAndPathConstants.digest, "012345"), //
				JarAndPathConstants.groupId, "some.group", JarAndPathConstants.artifactId, "someArtifact", JarAndPathConstants.version, "someVersion");
	}

	private void checkCanPullJarNameData() {
		checkCanPullData(CardConstants.jarNameUrlKey, //
				Maps.stringObjectMap(CollectionConstants.jarStem, "ant"), //
				CommonConstants.typeTag, CommonConstants.collectionType); // under the collection are the individual values
	}

	private void checkCanPullRepositoryData() {
		checkCanPullData(CardConstants.artifactUrlKey, //
				Maps.stringObjectMap(JarAndPathConstants.groupId, "some.group", JarAndPathConstants.artifactId, "someArtifact"), //
				CommonConstants.typeTag, CardConstants.artifact); // under the collection are the individual values

	}

	private void checkCanPullData(String urlKey, Map<String, Object> urlMap, Object... expectedNamesAndValues) {
		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(urlKey);
		String url = jarUrlGenerator.findUrlFor(urlMap);
		Map<String, Object> expectedJarData = Maps.stringObjectMap(expectedNamesAndValues);
		assertEquals(expectedJarData, gitLocal.getFile(IFileDescription.Utils.plain(url)));
		assertEquals(expectedJarData, Json.parseFile(new File(localRoot, Urls.compose(url, CommonConstants.dataFileName))));
	}

	private void checkRuntimeJarPopulated() {
		Map<String, Object> urlMap = Maps.stringObjectLinkedMap(CollectionConstants.jarStem, "ant");
		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarNameUrlKey);
		String url = jarUrlGenerator.findUrlFor(urlMap);
		File antDirectory = new File(remoteRoot, url);
		assertEquals(Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), Json.mapFromString(Files.getText(new File(antDirectory, CommonConstants.dataFileName))));
		// the actual new jar is in a sub directory. .git should be the only other directory
		File[] antSubDirectories = Files.listChildDirectoriesIgnoringDot(antDirectory);
		assertEquals(1, antSubDirectories.length);
		File subDirectory = antSubDirectories[0];
		assertEquals(Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.jarName, CardConstants.group, "some.group", JarAndPathConstants.artifactId, "someArtifact"), //
				Json.mapFromString(Files.getText(new File(subDirectory, CommonConstants.dataFileName))));
	}

	private void checkRepositoryPopulated(String digest, String group, String artifact, String version) {
		Map<String, Object> groupArtifactVersion = Maps.stringObjectLinkedMap(JarAndPathConstants.groupId, group, JarAndPathConstants.artifactId, artifact, JarAndPathConstants.version, version);
		Map<String, Object> groupArtifactVersionDigest = Maps.with(groupArtifactVersion, JarAndPathConstants.digest, digest);

		IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
		String jarUrl = jarUrlGenerator.findUrlFor(groupArtifactVersionDigest);
		assertEquals(groupArtifactVersion, Json.mapFromString(Files.getText(new File(remoteRoot, Urls.compose(jarUrl, CommonConstants.dataFileName)))));

		IUrlGenerator artifactGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
		String artifactUrl = artifactGenerator.findUrlFor(groupArtifactVersion);
		assertEquals(Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.artifact), Json.mapFromString(Files.getText(new File(remoteRoot, Urls.compose(artifactUrl, CommonConstants.dataFileName)))));

	}

}