/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.internal.Card;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleSpec;

public class CardMain {
	public static void main(String[] args) {
		final IMutableCardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();

		Swts.Show.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore).withTitleSpecFn(new IFunction1<ICardData, TitleSpec>() {
					@Override
					public TitleSpec apply(ICardData card) throws Exception {
						Color white = from.getDisplay().getSystemColor(SWT.COLOR_WHITE);
						return TitleSpec.noTitleSpec(white, white);
					}
				});
				ICard card = new Card(from, cardConfig, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}
}