/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.dataStore.internal.CardDataStoreForRepository;
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.swt.Swts;
import org.springframework.core.io.ClassPathResource;

public class SnippetIntegrationTest extends AbstractExplorerIntegrationTest {

	@SuppressWarnings("unchecked")
	public void testAddNewSnippet() {
		displayCard(snippetUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = createPopupMenu(card);
				String addNewSnippet = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CardConstants.menuItemAddSnippet);
				executeMenuItem(menu, addNewSnippet);
				IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
				assertFalse(detailContent.getFooter().isOkEnabled());

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

						File file = new File(localRoot, Urls.compose(card.url(), key, CommonConstants.dataFileName));
						assertEquals(Maps.stringObjectMap("title", "New Title", "description", "someDescription", "content", "some\nContent", CommonConstants.typeTag, CardConstants.snippet), Json.parseFile(file));
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
				IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
				assertFalse(detailContent.getFooter().isOkEnabled());
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
		IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
		assertTrue(detailContent.getFooter().isOkEnabled());
		Swts.Buttons.press( detailContent.getFooter().okButton());
	}

	@SuppressWarnings("unchecked")
	private void clickCancelButton() {
		IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
		Swts.Buttons.press(detailContent.getFooter().cancelButton());
	}

	@SuppressWarnings("unchecked")
	private void checkTextEditorAndChangeTo(int index, String expected, String newValue) {
		IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		Text text = (Text) snippetComposite.getChildren()[index*2+1];
		assertEquals(expected, text.getText());
		text.setText(newValue);
		text.notifyListeners(SWT.Modify, new Event());
	}

	@SuppressWarnings("unchecked")
	private void checkStyledTextEditorAndChangeTo(int index, String expected, String newValue) {
		IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		StyledText text = (StyledText) snippetComposite.getChildren()[index*2+1];
		assertEquals(expected, text.getText());
		text.setText(newValue);
		text.notifyListeners(SWT.Modify, new Event());
	}

	@SuppressWarnings("unchecked")
	private void checkLabel(int labelIndex, String expected) {
		IDataCompositeWithOkCancel<Composite> detailContent = (IDataCompositeWithOkCancel<Composite>) masterDetailSocial.getDetailContent();
		Composite snippetComposite = detailContent.getEditor();
		Label label = (Label) snippetComposite.getChildren()[labelIndex*2];
		assertEquals(expected, label.getText());
	}

	@Override
	protected void setUp() throws Exception {
		DOMConfigurator.configure(new ClassPathResource("log4j.xml").getURL());
		Logger.getRootLogger().setLevel(Level.FATAL);
		CardDataStoreForRepository.logger.setLevel(Level.DEBUG);
		super.setUp();
		// this is a bit of a bodge...it ensures that the snippet menu is activated (no matter what url).
		String popupMenuId = getClass().getSimpleName();
		ICardMenuItemHandler.Utils.addSnippetMenuItemHandlers(explorer, popupMenuId);
	}

}