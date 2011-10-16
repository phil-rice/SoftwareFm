package org.softwareFm.eclipse.card;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.json.simple.JSONValue;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.api.ICard;
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

			private Listener listener;
			private Group group;

			@Override
			public void selected(IHasComposite hasControl, String context, Object value) throws Exception {
				Composite content = hasControl.getComposite();
				if (listener != null)
					group.removeListener(SWT.Resize, listener);
				Swts.removeAllChildren(content);

				ICardDataStore cardDataStore = new CardDataStoreMock(context, JSONValue.parse(value.toString()));
				ICard card = cardFactory.makeCard(content, cardDataStore, context);
				final Composite cardComposite = card.getComposite();
				listener = new Listener() {
					@Override
					public void handleEvent(Event event) {
						layoutCard(cardComposite);
					}
				};
				group.addListener(SWT.Resize, listener);
				layoutCard(cardComposite);
				System.out.println();
				Swts.layoutAsString(content);
			}

			private void layoutCard(final Composite cardComposite) {
				Rectangle clientArea = group.getClientArea();
				Point size = cardComposite.computeSize(clientArea.width, clientArea.height);
				cardComposite.setSize(size);
				cardComposite.layout();
			}

			@Override
			public IHasComposite makeChild(Composite parent) throws Exception {
				group = new Group(parent, SWT.NULL);
				group.setText("parent");
				Swts.resizeMeToParentsSize(group);
				return new IHasComposite() {

					@Override
					public Control getControl() {
						return group;
					}

					@Override
					public Composite getComposite() {
						return group;
					}
				};
			}
		});
	}
}
