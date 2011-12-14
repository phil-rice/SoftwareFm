package org.softwareFm.collections.explorer.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.composites.CompositeWithCardMargin;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.card.title.TitleWithTitlePaintListener;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.ExplorerAdapter;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class ExplorerIntegrationTest extends AbstractExplorerIntegrationTest {

	public void testShowContentOnlyAsksForOneMainUrlFromCardDataStore() {
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
						assertEquals(rootUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
					}
				});
				explorer.showContents();
				dispatchUntilTimeoutOrLatch(latch, delay);

			}
		});
		assertEquals(1, count.get());
	}

	public void testViewTutorials() {
		displayCardThenViewChild(AbstractExplorerIntegrationTest.artifactUrl, "Tutorials", new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertEquals(rootUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
				assertEquals(explorer.getBrowser().getComposite(), masterDetailSocial.getDetailContent());
			}
		});
	}

	public void testAddMailingListCausesCardEditorToAppear() {
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

	public void testUnrecognisedJarPutsJarNotRecognisedTextInLhsAndLeavesDetailAndSocialAlone() {
		IHasControl detail = masterDetailSocial.createAndShowDetail(Swts.labelFn("detail"));
		IHasControl social = masterDetailSocial.createAndShowSocial(Swts.labelFn("social"));

		File file = new File("a/b/c/artifact-1.0.0.jar");
		explorer.displayUnrecognisedJar(file, "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());
		String pattern = IResourceGetter.Utils.getOrException(rawResourceGetter, CollectionConstants.jarNotRecognisedText);
		String expected = Strings.removeNewLines(MessageFormat.format(pattern, file, file.getName(), "someProject")).replace("<", "").replace(">", "");
		assertEquals(expected.trim(), Strings.removeNewLines(text.getText()));

		assertSame(detail.getControl(), masterDetailSocial.getDetailContent());
		assertSame(social.getControl(), masterDetailSocial.getSocialContent());
	}

	@SuppressWarnings("unchecked")
	public void testClickingOnUnrecognisedJarOpensEditor() {
		explorer.displayUnrecognisedJar(new File("a/b/c/artifact-1.0.0.jar"), "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());

		text.notifyListeners(SWT.MouseUp, new Event());
		Control detailContent = masterDetailSocial.getDetailContent();
		final IValueComposite<Table> valueComposite = (IValueComposite<Table>) detailContent;
		TitleWithTitlePaintListener titleWithTitlePaintListener = valueComposite.getTitle();
		String jarTitle = IResourceGetter.Utils.getOrException(rawResourceGetter, CollectionConstants.jarNotRecognisedTitle);
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
		doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
			@Override
			public void run() {
				Button okButton = valueComposite.getOkCancel().okButton;
				okButton.notifyListeners(SWT.Selection, new Event());
			}
		}, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertEquals(rootUrl + "/some/group/id/some.group.id/artifact/someArtifact", card.url());
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

	private void checkUnrecognisedOk(final boolean expected, final String group, final String artifact, final String version) {
		explorer.displayUnrecognisedJar(new File("a/b/c/artifact-1.0.0.jar"), "someDigest", "someProject");
		StyledText text = getTextInBorderComponent(masterDetailSocial.getMasterContent());

		text.notifyListeners(SWT.MouseUp, new Event());
		Control detailContent = masterDetailSocial.getDetailContent();
		final IValueComposite<Table> valueComposite = (IValueComposite<Table>) detailContent;
		TitleWithTitlePaintListener titleWithTitlePaintListener = valueComposite.getTitle();
		String jarTitle = IResourceGetter.Utils.getOrException(rawResourceGetter, CollectionConstants.jarNotRecognisedTitle);
		assertEquals(jarTitle, titleWithTitlePaintListener.getText());
		Table editor = valueComposite.getEditor();
		checkAndEdit(editor, new IAddingCallback<Table>() {
			@Override
			public void process(boolean added, Table card, IAdding adding) {
				adding.tableItem(0, "Group Id", "Please specify the group id", group);
				adding.tableItem(1, "Artifact Id", "artifact", artifact);
				adding.tableItem(2, "Version", "1.0.0",version);
				assertEquals(expected, valueComposite.getOkCancel().okButton.isEnabled());
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
		String view = "View";
		String edit = "Edit";

		checkMenu(0, "Name", edit);
		checkMenu(1, "Url", edit);
		checkMenu(2, "Description", edit);
		checkMenu(3, "Issues", edit);
		checkMenu(4, "Download Url", edit);
		checkMenu(5, "Documentation", view, "Add documentation");
		checkMenu(6, "Mailing List", view, "Add mailingList");
		checkMenu(7, "Tutorials", view, "Add tutorial");
		checkMenu(8, "Tweet", view, "Add tweet");
		checkMenu(9, "Rss", view, "Add rss");
		checkMenu(10, "Forum", view, "Add forum");
		checkMenu(11, "Article", view, "Add article");
		checkMenu(12, "Companies", view, "Add company");
		checkMenu(13, "Adverts", view, "Add advert");
		checkMenu(14, "Job", view, "Add job");
		checkMenu(15, "Version", view, "Add version");
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
				cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu, card);
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
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}

}
