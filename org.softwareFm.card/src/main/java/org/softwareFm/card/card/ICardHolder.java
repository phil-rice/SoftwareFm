/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.internal.CardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.utilities.callbacks.ICallback;

/** The card holder includes the title for the card. Typically it is displayed before the card data has been found */
public interface ICardHolder extends IHasComposite {
	void setCard(ICard card);

	void addLineSelectedListener(ILineSelectedListener iLineSelectedListener);

	void addCardChangedListener(ICardChangedListener listener);

	void addCardTitleSelectedListener(ICardSelectedListener listener);

	ICard getCard();

	public static class Utils {
		public static ICardHolder cardHolderWithLayout(Composite parent, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
			CardHolder cardHolder = new CardHolder(parent, cardConfig, rootUrl, callbackToGotoUrl);
			cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
			return cardHolder;
		}
	}

}