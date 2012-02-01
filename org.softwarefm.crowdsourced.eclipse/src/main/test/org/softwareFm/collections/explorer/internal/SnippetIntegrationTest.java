package org.softwareFm.collections.explorer.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SnippetIntegrationTest extends AbstractExplorerIntegrationTest {

	@SuppressWarnings("unchecked")
	public void testAddNewSnippet() {
		displayCard(snippetUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = createPopupMenu(card);
				String addNewSnippet = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CardConstants.menuItemAddSnippet);
				executeMenuItem(menu, addNewSnippet);
				IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
				assertFalse(detailContent.getOkCancel().isOkEnabled());

				checkLabel(0, "Title");
				checkLabel(1, "Description");
				checkLabel(2, "Content");
				checkTextEditorAndChangeTo(0, "", "New Title");
				checkTextEditorAndChangeTo(1, "<Please add a description>", "someDescription");
				checkStyledTextEditorAndChangeTo(2, "", "some\nContent");
				doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
					@Override
					public void run() {
						clickOkButton();
					}
				}, new CardHolderAndCardCallback() {
					@Override
					public void process(ICardHolder cardHolder, ICard card) throws Exception {
						Map<String, Object> data = card.data();
						assertEquals(1, card.getTable().getItemCount());
						String key = (String) card.getTable().getItem(0).getData();
						assertEquals(2, data.size());
						assertEquals(CardConstants.collection, data.get(CardConstants.slingResourceType));

						Map<String, Object> addedValue = (Map<String, Object>) data.get(key);
						assertEquals("New Title", addedValue.get("title"));
						assertEquals("someDescription", addedValue.get("description"));
						assertEquals("some\nContent", addedValue.get("content"));
						assertEquals(CardConstants.snippet, addedValue.get(CardConstants.slingResourceType));
					}
				});

			}

		});
	}

	public void testValidation() {
		checkValidation("", "someDescription", "some\nContent");
		checkValidation("someTitle", "someDescription", "");
		checkValidation("someTitle", "<mustnt start with ','", "some\nContent");
	}

	public void testOnlyAddsTitleDescriptionContentToRepo() {
		fail();
	}

	@SuppressWarnings("unchecked")
	private void checkValidation(final String newTitle, final String newDescription, final String newContent) {
		displayCard(snippetUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = createPopupMenu(card);
				String addNewSnippet = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CardConstants.menuItemAddSnippet);
				executeMenuItem(menu, addNewSnippet);
				checkLabel(0, "Title");
				checkLabel(1, "Description");
				checkLabel(2, "Content");
				checkTextEditorAndChangeTo(0, "", newTitle);
				checkTextEditorAndChangeTo(1, "<Please add a description>", newDescription);
				checkStyledTextEditorAndChangeTo(2, "", newContent);
				IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
				assertFalse(detailContent.getOkCancel().isOkEnabled());
			}

		});
	}

	public void testCancel() {
		displayCard(snippetUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = createPopupMenu(card);
				String addNewSnippet = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CardConstants.menuItemAddSnippet);
				executeMenuItem(menu, addNewSnippet);
				checkLabel(0, "Title");
				checkLabel(1, "Description");
				checkLabel(2, "Content");
				checkTextEditorAndChangeTo(0, "", "New Title");
				checkTextEditorAndChangeTo(1, "<Please add a description>", "someDescription");
				checkStyledTextEditorAndChangeTo(2, "", "some\nContent");
				doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
					@Override
					public void run() {
						clickCancelButton();
					}
				}, new CardHolderAndCardCallback() {
					@Override
					public void process(ICardHolder cardHolder, ICard card) throws Exception {
						assertEquals(0, card.getTable().getItemCount());
						assertNull(masterDetailSocial.getDetailContent());// the editor has been removed
					}
				});
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void clickOkButton() {
		IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
		assertTrue(detailContent.getOkCancel().isOkEnabled());
		Swts.Buttons.press(detailContent.getOkCancel().okButton);
	}

	@SuppressWarnings("unchecked")
	private void clickCancelButton() {
		IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
		Swts.Buttons.press(detailContent.getOkCancel().cancelButton);
	}

	@SuppressWarnings("unchecked")
	private void checkTextEditorAndChangeTo(int index, String expected, String newValue) {
		IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		Composite editors = (Composite) snippetComposite.getChildren()[1];
		Text text = (Text) editors.getChildren()[index];
		assertEquals(expected, text.getText());
		text.setText(newValue);
		text.notifyListeners(SWT.Modify, new Event());
	}

	@SuppressWarnings("unchecked")
	private void checkStyledTextEditorAndChangeTo(int index, String expected, String newValue) {
		IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		Composite editors = (Composite) snippetComposite.getChildren()[1];
		StyledText text = (StyledText) editors.getChildren()[index];
		assertEquals(expected, text.getText());
		text.setText(newValue);
		text.notifyListeners(SWT.Modify, new Event());
	}

	@SuppressWarnings("unchecked")
	private void checkLabel(int labelIndex, String expected) {
		IValueComposite<Composite> detailContent = (IValueComposite<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		Composite labels = (Composite) snippetComposite.getChildren()[0];
		Label label = (Label) labels.getChildren()[labelIndex];
		assertEquals(expected, label.getText());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// this is a bit of a bodge...it ensures that the snippet menu is activated (no matter what url).
		String popupMenuId = getClass().getSimpleName();
		ICardMenuItemHandler.Utils.addSnippetMenuItemHandlers(explorer, popupMenuId);
	}

}
