package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.json.simple.JSONValue;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryWithAggregateAndSort;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;

public class CardUnit {
	public static void main(String[] args) throws IOException {
		final File root = new File("../org.softwareFm.card/src/test/resources/org/softwareFm/card/json").getCanonicalFile();
		final ICardFactoryWithAggregateAndSort cardFactory = ICardFactory.Utils.cardFactoryWithAggregateAndSort();
		Swts.xUnit(CardUnit.class.getSimpleName(), root, "json", new ISituationListAndBuilder<IHasComposite>() {

			@Override
			public void selected(IHasComposite hasControl, String context, Object value) throws Exception {
				Composite content = hasControl.getComposite();
				Swts.removeAllChildren(content);
				ICardDataStore cardDataStore = new CardDataStoreMock(context, JSONValue.parse(value.toString()));
				cardFactory.makeCard(content, cardDataStore, context);
				System.out.println();
				Swts.layoutAsString(content);
			}

			@Override
			public IHasComposite makeChild(Composite parent) throws Exception {
				final Group result = new Group(parent, SWT.NULL);
				result.setText("parent");
				Swts.resizeMeToParentsSize(result);
				return new IHasComposite() {

					@Override
					public Control getControl() {
						return result;
					}

					@Override
					public Composite getComposite() {
						return result;
					}
				};
			}
		});
	}
}
