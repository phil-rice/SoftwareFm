/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.NameAndValuesEditor;
import org.softwareFm.swt.swt.Swts;

public class NameAndValuesEditorMain {
	public static void main(String[] args) {
		Swts.Show.display(NameAndValuesEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				Map<String, Object> data = Maps.stringObjectMap("title", "Some title", "description", "Some Description", "content", "public void testThis(){\n  fail();\n}\n");
				final String cardType = "";
				NameAndValuesEditor editor = new NameAndValuesEditor(from, cardConfig, cardType, "someTitle", "tutorial", data, Arrays.asList(//
						INamesAndValuesEditor.Utils.text(cardConfig, "email"),//
						INamesAndValuesEditor.Utils.text(cardConfig, "password"),//
						INamesAndValuesEditor.Utils.text(cardConfig, "confirmPassword"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, "address"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, "comment")), new ICardEditorCallback() {
	
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