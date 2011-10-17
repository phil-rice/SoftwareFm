package org.softwareFm.eclipse.card;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.MultipleCardsWithScroll;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;

public class PathAndDetails implements IHasComposite {

	private final Composite content;

	static class PathAndDetailsComposite extends SashForm {
		private final List<String> urls = Lists.newList();
		private final Composite details;
		private final MultipleCardsWithScroll multipleCards;

		public PathAndDetailsComposite(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory, String initialUrl) {
			super(parent, SWT.NULL);
			ICallback<ICard> callbackWhenPopulated = new ICallback<ICard>() {
				@Override
				public void process(ICard t) throws Exception {
					t.getComposite().layout();
				}
			};
			cardFactory.makeCard(parent, cardDataStore, initialUrl, callbackWhenPopulated);
			details = new Composite(parent, SWT.NULL);
			details.setLayout(new StackLayout());

			multipleCards = new MultipleCardsWithScroll(details, cardDataStore, cardFactory);
			setWeights(new int[] { 1, 3 });
		}

	}

	public PathAndDetails(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory, String initialUrl) {
		content = new PathAndDetailsComposite(parent, cardDataStore, cardFactory, initialUrl);

	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

}
