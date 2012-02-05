package org.softwareFm.swt;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.internal.NameAndValuesEditor;
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
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email"),//
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "password"),//
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "confirmPassword"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "address"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "comment")), new ICardEditorCallback() {
	
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

