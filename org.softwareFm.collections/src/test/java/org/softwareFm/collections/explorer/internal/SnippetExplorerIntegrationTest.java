package org.softwareFm.collections.explorer.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.collections.explorer.BrowserAndNavBar;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.utilities.collections.Lists;

public class SnippetExplorerIntegrationTest extends AbstractExplorerIntegrationTest {

	public void testTestAddingSnippet() {
		displayCard(snippetUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				final Menu menu = new Menu(shell);
				cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu, card);
				executeMenuItem(menu, "Add new snippet");
				final IValueComposite<Table> composite = (IValueComposite<Table>) masterDetailSocial.getDetailContent();
				Table table = composite.getEditor();
				checkAndEdit(table, new IAddingCallback<Table>() {
					@Override
					public void process(boolean added, Table data, IAdding adding) {
						adding.tableItem(0, "Title", "", "someTitle");
						adding.tableItem(1, "Description", "<Please add a description>", "someDescription");
						adding.tableItem(2, "Content", "", "someContent");
					}
				});
				assertEquals(3, table.getItemCount());
				ICard finalCard = doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
					@Override
					public void run() {
						Button okButton = composite.getOkCancel().okButton;
						okButton.notifyListeners(SWT.Selection, new Event());
					}
				}, new CardHolderAndCardCallback() {
					@Override
					public void process(ICardHolder cardHolder, ICard card) throws Exception {
						System.out.println(card);
					}
				});
				// At this point we have clicked OK, created the content, and are viewing the list of snippets, with the last one we just added selected
				Control masterContent = masterDetailSocial.getMasterContent();
				Table masterTable = Lists.getOnly(Swts.findChildrenWithClass(masterContent, Table.class));
				assertEquals(finalCard.getTable(), masterTable);
				int selectionIndex = masterTable.getSelectionIndex();
				TableItem selectedItem = masterTable.getItem(selectionIndex);
				assertEquals("someTitle", selectedItem.getText(0));
				
				BrowserAndNavBar browser = explorer.getBrowser();
				assertEquals(browser.getControl(), masterDetailSocial.getDetailContent());
				assertEquals(new PlayItem(DisplayConstants.snippetFeedType, rootUrl+snippetUrl +"/" + selectedItem.getData()), browser.playItem());
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardMenuItemHandler.Utils.addSnippetMenuItemHandlers(explorer, "popupmenuid");
	}

}
