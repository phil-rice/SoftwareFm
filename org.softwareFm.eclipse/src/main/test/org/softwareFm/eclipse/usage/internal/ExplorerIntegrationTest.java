/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.composites.CompositeWithCardMargin;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.explorer.ExplorerAdapter;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleWithTitlePaintListener;

public class ExplorerIntegrationTest extends AbstractExplorerIntegrationTest {
	private String popupMenuId;

	public void testRefresh() {
		postArtifactData();
		displayCard(artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				cardConfig.cardDataStore.refresh(card.url());// this is the equivalent of pressing the refresh button
				File localFile = new File(localRoot, card.url());
				assertFalse(localFile.exists());
			}
		});
	}

	public void testUnrecognisedJarPutsJarNotRecognisedTextInLhsAndLeavesDetailAloneWhileDisplayingHelpInSocial() {
		IHasControl detail = masterDetailSocial.createAndShowDetail(Swts.labelFn("detail"));

		File file = new File("a/b/c/artifact-1.0.0.jar");
		explorer.displayUnrecognisedJar(file, "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());
		String pattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedText);

		String expected = Strings.removeNewLines(MessageFormat.format(pattern, file, file.getName(), "someProject")).replace("<", "").replace(">", "");
		assertEquals(expected.trim(), Strings.removeNewLines(text.getText()));

		assertSame(detail.getControl(), masterDetailSocial.getDetailContent());
		CompositeWithCardMargin socialContent = (CompositeWithCardMargin) masterDetailSocial.getSocialContent();
		StyledText actualHelpTextComponent =  Swts.<StyledText>getDescendant(socialContent, 1, 0, 0);
		String expectedHelp = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.helpUnrecognisedPleaseAddText);
		assertEquals(expectedHelp, actualHelpTextComponent.getText());
	}

	@SuppressWarnings("unchecked")
	public void testClickingOnUnrecognisedJarOpensEditor() {
		explorer.displayUnrecognisedJar(new File("a/b/c/artifact-1.0.0.jar"), "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());

		text.notifyListeners(SWT.MouseUp, new Event());
		Control detailContent = masterDetailSocial.getDetailContent();
		final IDataCompositeWithOkCancel<Table> valueComposite = (IDataCompositeWithOkCancel<Table>) detailContent;
		TitleWithTitlePaintListener titleWithTitlePaintListener = valueComposite.getTitle();
		String jarTitle = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedTitle);
		assertEquals(jarTitle, titleWithTitlePaintListener.getText());
		Table editor = valueComposite.getEditor();
		checkAndEdit(editor, new IAddingCallback<Table>() {
			@Override
			public void process(boolean added, Table card, IAdding adding) {
				adding.tableItem(0, "Group Id", "Please specify the group id", "some.group.id");
				adding.tableItem(1, "Artifact Id", "artifact", "someArtifact");
				adding.tableItem(2, "Version", "1.0.0", "1.2.0");
			}
		});
		doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {//one intermittent failing test
			@Override
			public void run() {
				Control okButton =  valueComposite.getFooter().okButton();
				Swts.Buttons.press(okButton);
			}
		}, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertEquals(rootArtifactUrl + "/some/group/id/some.group.id/artifact/someArtifact", card.url());
			}

		});
	}

	public void testShowContentOnlyAsksForOneMainUrlFromCardDataStore() {
		postArtifactData();
		final AtomicInteger count = new AtomicInteger();
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws InterruptedException {
				selectItem(card, "Tutorials");
				final CountDownLatch latch = new CountDownLatch(1);
				explorer.addExplorerListener(new ExplorerAdapter() {
					@Override
					public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
						assertEquals(1, count.incrementAndGet());
						latch.countDown();
						assertEquals(rootArtifactUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
					}
				});
				explorer.showContents();
				dispatchUntilTimeoutOrLatch(latch, CommonConstants.testTimeOutMs);

			}
		});
		assertEquals(1, count.get());
	}

	public void testViewTutorials() {
		postArtifactData();
		displayCardThenViewChild(AbstractExplorerIntegrationTest.artifactUrl, "Tutorials", new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertEquals(rootArtifactUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
				assertEquals(explorer.getBrowser().getComposite(), masterDetailSocial.getDetailContent());
			}
		});
	}

	public void testAddMailingListCausesCardEditorToAppear() {
		postArtifactData();
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = selectAndCreatePopupMenu(card, "Mailing List");
				executeMenuItem(menu, "Add mailingList");

				Composite detailContent = (Composite) masterDetailSocial.getDetailContent();
				assertEquals("CardEditorComposite", detailContent.getClass().getSimpleName());
				assertEquals(2, detailContent.getChildren().length);// title,body
			}
		});
	}

	public void testUnrecognisedJarEditorOnlyEnablesOkIfLegalValues() {
		checkUnrecognisedOk(false, "", "", "");
		checkUnrecognisedOk(true, "g", "a", "v");
		checkUnrecognisedOk(true, "g-.102938", "a-.sdlkfj", "v-1.-sdlfkj");
		checkUnrecognisedOk(false, " g", "a", "v");
		checkUnrecognisedOk(false, "g", " a", "v");
		checkUnrecognisedOk(false, "g", " a", " v");

	}

	@SuppressWarnings("unchecked")
	private void checkUnrecognisedOk(final boolean expected, final String group, final String artifact, final String version) {
		explorer.displayUnrecognisedJar(new File("a/b/c/artifact-1.0.0.jar"), "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());

		text.notifyListeners(SWT.MouseUp, new Event());
		Control detailContent = masterDetailSocial.getDetailContent();
		final IDataCompositeWithOkCancel<Table> valueComposite = (IDataCompositeWithOkCancel<Table>) detailContent;
		TitleWithTitlePaintListener titleWithTitlePaintListener = valueComposite.getTitle();
		String jarTitle = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedTitle);
		assertEquals(jarTitle, titleWithTitlePaintListener.getText());
		Table editor = valueComposite.getEditor();
		checkAndEdit(editor, new IAddingCallback<Table>() {
			@Override
			public void process(boolean added, Table card, IAdding adding) {
				adding.tableItem(0, "Group Id", "Please specify the group id", group);
				adding.tableItem(1, "Artifact Id", "artifact", artifact);
				adding.tableItem(2, "Version", "1.0.0", version);
				assertEquals(expected, valueComposite.getFooter().okButton().isEnabled());
			}
		});

	}

	private StyledText getTextInBorderComponent(Control control) {
		CompositeWithCardMargin composite = (CompositeWithCardMargin) control;
		Control[] children = composite.getChildren();
		assertEquals(2, children.length);
		assertTrue(children[0] instanceof Canvas);// title
		Composite body = (Composite) children[1];
		Control[] bodyChildren = body.getChildren();
		assertEquals(1, bodyChildren.length);
		Composite innerBody = (Composite) body.getChildren()[0];
		StyledText text = (StyledText) innerBody.getChildren()[0];
		return text;
	}

	public void testTitlesAndRightClickMenusText() {
		postArtifactData();
		String view = "View";
		String edit = "Edit";
		String help = "Help";

		checkMenu(0, "Name", edit, help);
		checkMenu(1, "Url", edit, help);
		checkMenu(2, "Description", edit, help);
		checkMenu(3, "Issues", edit, help);
		checkMenu(4, "Download Url", edit, help);
		checkMenu(5, "Documentation", view, "Add documentation", help);
		checkMenu(6, "Mailing List", view, "Add mailingList", help);
		checkMenu(7, "Tutorials", view, "Add tutorial", help);
		checkMenu(8, "Tweet", view, "Add tweet", help);
		checkMenu(9, "Rss", view, "Add rss", help);
		checkMenu(10, "Forum", view, "Add forum", help);
		checkMenu(11, "Article", view, "Add article", help);
		checkMenu(12, "Companies", view, "Add company", help);
		checkMenu(13, "Adverts", view, "Add advert", help);
		checkMenu(14, "Job", view, "Add job", help);
		checkMenu(15, "Version", view, "Add version", help);
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) {
				Table table = card.getTable();
				assertEquals(17, table.getItemCount()); // currently has comments, which will be vanishing in near future.
			}
		});
	}

	private void checkMenu(final int index, final String expectedName, final String... expected) {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) {
				final Menu menu = new Menu(shell);
				Table table = card.getTable();
				table.select(index);
				cardConfig.popupMenuService.contributeTo(popupMenuId, new Event(), menu, card);
				List<String> actual = Lists.newList();
				for (int i = 0; i < menu.getItemCount(); i++)
					actual.add(menu.getItem(i).getText());
				assertEquals(expectedName, table.getItem(index).getText(0));
				List<String> fullExpected = Lists.append(Arrays.asList(expected));
				assertEquals(fullExpected, actual);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		popupMenuId = getClass().getSimpleName();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, popupMenuId);
	}

}