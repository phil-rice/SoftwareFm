package org.softwareFm.eclipse.card;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.MultipleCards;
import org.softwareFm.card.api.MultipleCardsWithScroll;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class CardGuis {
public static void main(String[] args) {
	Swts.display(CardGuis.class.getSimpleName(), new IFunction1<Composite, Composite>() {
		@Override
		public Composite apply(Composite from) throws Exception {
			Composite content = new Composite(from, SWT.NULL);
			Swts.makeButtonFromMainMethod(content, CardUnit.class);
			Swts.makeButtonFromMainMethod(content, CardFromUrlUnit.class);
			Swts.makeButtonFromMainMethod(content, CardExplorer.class);
			Swts.makeButtonFromMainMethod(content, MultipleCards.class);
			Swts.makeButtonFromMainMethod(content, MultipleCardsWithScroll.class);
			Swts.addGrabHorizontalAndFillGridDataToAllChildren(content);
			return content;
		}
	});
}
}
