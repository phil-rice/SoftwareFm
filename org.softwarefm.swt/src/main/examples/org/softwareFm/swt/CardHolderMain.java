package org.softwareFm.swt;

import java.util.Arrays;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.future.GatedMockFuture;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.internal.CardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.swt.Swts.Show;
import org.softwareFm.swt.swt.Swts.Size;

public class CardHolderMain {
	public static void main(String[] args) {
			Show.display(CardHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final CardConfig cardConfig = CardDataStoreFixture.asyncCardConfig(from.getDisplay());
					final ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(from, cardConfig, Arrays.asList(CardDataStoreFixture.url), ICallback.Utils.<String> noCallback());
					final Future<ICard> future = ICardFactory.Utils.makeCard(cardHolder, cardConfig, CardDataStoreFixture.url1a, new ICallback<ICard>() {
						@Override
						public void process(ICard card) throws Exception {
							if (card == null)
								return;
							cardHolder.setCard(card);
						}
					});
					new Thread() {
						@Override
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								throw WrappedException.wrap(e);
							}
							((GatedMockFuture<?, ?>) future).kick();
						}
					}.start();
					Size.resizeMeToParentsSize(cardHolder.getControl());
					return cardHolder.getComposite();
				}
			});
		}
}

