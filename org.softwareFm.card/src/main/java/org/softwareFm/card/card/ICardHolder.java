package org.softwareFm.card.card;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.internal.CardHolder;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.utilities.callbacks.ICallback;

/** The card holder includes the title for the card. Typically it is displayed before the card data has been found */
public interface ICardHolder extends IHasComposite {
	void setCard(ICard card);

	void addLineSelectedListener(ILineSelectedListener iLineSelectedListener);

	void addCardChangedListener(ICardChangedListener listener);

	ICard getCard();

	public static class Utils {
		public static ICardHolder cardHolderWithLayout(Composite parent, String loadingText, String title, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
			CardHolder cardHolder = new CardHolder(parent, loadingText, title, cardConfig, rootUrl, callbackToGotoUrl);
			cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
			return cardHolder;
		}
	}

}
