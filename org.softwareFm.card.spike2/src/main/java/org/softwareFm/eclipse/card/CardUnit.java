package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.json.simple.JSONValue;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.HasComposite;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;

public class CardUnit {
	public static void main(String[] args) throws IOException {
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		final File root = new File("../org.softwareFm.card/src/test/resources/org/softwareFm/card/json").getCanonicalFile();
		String extension = "json";
		Swts.xUnit(CardUnit.class.getSimpleName(), root, extension, new ISituationListAndBuilder<IHasComposite>() {

			@Override
			public void selected(IHasComposite hasControl, String context, Object value) throws Exception {
				Composite content = hasControl.getComposite();
				Swts.removeAllChildren(content);

				ICardDataStore cardDataStore = new CardDataStoreMock(context, JSONValue.parse(value.toString()));
				final ICard card = cardFactory.makeCard(content, cardDataStore, context);
				Swts.resizeMeToParentsSize(card.getControl());
				Swts.setSizeFromClientArea(hasControl, card);
			}

			@Override
			public IHasComposite makeChild(Composite parent) throws Exception {
				return new HasComposite(parent, SWT.NULL);
			}
		});
	}
}
