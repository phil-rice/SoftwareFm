package org.softwareFm.card.api;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;

public class MultipleCardsWithScroll implements IHasComposite {

	private final ScrolledComposite scroll;
	public final MultipleCards content;

	public MultipleCardsWithScroll(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory) {
		scroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		content = new MultipleCards(scroll, cardDataStore, cardFactory);
		scroll.setContent(content.getControl());
	}

	@Override
	public Control getControl() {
		return scroll;
	}

	@Override
	public Composite getComposite() {
		return scroll;
	}

	public static void main(String[] args) {
		final CardDataStoreMock cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
		Swts.displayNoLayout(MultipleCardsWithScroll.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final MultipleCardsWithScroll content = new MultipleCardsWithScroll(from, cardDataStore, cardFactory);
				ICallback<ICard> callbackWhenPopulated = new ICallback<ICard>() {
					@Override
					public void process(ICard t) throws Exception {
						layout(content.scroll, content);
					}
				};
				content.openCardAsChildOf(null, CardDataStoreFixture.url, callbackWhenPopulated);
				content.openCardAsChildOf(null, CardDataStoreFixture.url1a, callbackWhenPopulated);
				content.openCardAsChildOf(null, CardDataStoreFixture.url1b, callbackWhenPopulated);
				content.openCardAsChildOf(null, CardDataStoreFixture.url1a, callbackWhenPopulated);
				content.openCardAsChildOf(null, CardDataStoreFixture.url1b, callbackWhenPopulated);
				content.openCardAsChildOf(null, CardDataStoreFixture.url, callbackWhenPopulated);
				final Control control = content.getControl();
				from.addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event event) {
						Rectangle clientArea = from.getClientArea();
						control.setSize(clientArea.width, clientArea.height);
					}
				});
				from.addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event event) {
						layout(content.scroll, content);
					}
				});
				layout(content.scroll, content);
				return (Composite) control;
			}

			private void layout(final Composite from, final MultipleCardsWithScroll multipleCards) {
				Rectangle clientArea = from.getClientArea();
				Swts.setSizeToComputedAndLayout(multipleCards, clientArea);
				Swts.setSizeToComputedAndLayout(multipleCards.content, SWT.DEFAULT, SWT.DEFAULT);
				System.out.println("Scroll: " + multipleCards.scroll.getClientArea() + ", cards: " + multipleCards.content.getControl().getSize());
			}
		});
	}

	public ICard openCardAsChildOf(ICard parent, String url, ICallback<ICard> callbackWhenPopulated) {
		return content.openCardAsChildOf(parent, url, callbackWhenPopulated);
	}
}
