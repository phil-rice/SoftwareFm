package org.softwareFm.swt;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.internal.CardEditor;
import org.softwareFm.swt.editors.internal.ValueEditorLayout;
import org.softwareFm.swt.swt.Swts;

public class CardEditorMain {
	public static void main(String[] args) {
		Swts.Show.display(CardEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				CardEditor editor = new CardEditor(from, cardConfig, "someTitle", "someUrl", "tutorial", CardDataStoreFixture.data1aWithP1Q2, new ICardEditorCallback() {

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
				editor.getComposite().setLayout(new ValueEditorLayout());
				return editor.getComposite();
			}
		});
	}
}
