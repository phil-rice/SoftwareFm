/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.ICardConfigurator;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.KeyAndEditStrategy;
import org.softwareFm.swt.editors.NameAndValuesEditor;
import org.softwareFm.swt.swt.Swts;

public class SnippetEditor extends NameAndValuesEditor {
	private final static String cardType = CardConstants.snippet;

	public SnippetEditor(Composite parent, CardConfig cardConfig, String title, String url, Map<String, Object> initialData, ICardEditorCallback callback) {
		super(parent, cardConfig, cardType, title, url, initialData, getNamesAndValuesData(cardConfig), callback);
	}

	private static List<KeyAndEditStrategy> getNamesAndValuesData(CardConfig cardConfig) {
		return Arrays.asList(INamesAndValuesEditor.Utils.text(cardConfig, "title"),//
				INamesAndValuesEditor.Utils.text(cardConfig, "description"), //
				INamesAndValuesEditor.Utils.styledText(cardConfig, "content"));
	}

	public static void main(String[] args) {
		Swts.Show.display(SnippetEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = ICardConfigurator.Utils.basicConfigurator().configure(from.getDisplay(), new CardConfig(ICardFactory.Utils.noCardFactory(), new CardDataStoreMock()));
				Map<String, Object> data = Maps.stringObjectMap("title", "Some title", "description", "Some Description", "content", "public void testThis(){\n  fail();\n}\n");
				SnippetEditor editor = new SnippetEditor(from, cardConfig, "someTitle", "tutorial", data, new ICardEditorCallback() {

					@Override
					public void ok(ICardData cardData) {
						System.out.println("Ok: " + cardData.data());

					}

					@Override
					public void cancel(ICardData cardData) {
						System.out.println("Cancel: " + cardData.data());

					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
				return editor.getComposite();
			}
		});
	}

}