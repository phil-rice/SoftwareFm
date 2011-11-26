/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.configuration.internal.BasicCardConfigurator;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.internal.CardDataStoreForRepository;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.strings.Strings;

public class SingleCardExplorerUnit {

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/data";
		try {
			Show.xUnit(SingleCardExplorerUnit.class.getSimpleName(), new ISituationListAndBuilder<SingleCardExplorer>() {
				@Override
				public void selected(SingleCardExplorer hasControl, String context, Object value) throws Exception {
					hasControl.setUrl(Strings.nullSafeToString(value));
				}

				@Override
				public SingleCardExplorer makeChild(Composite parent) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(parent, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					return new SingleCardExplorer(parent, cardConfig, rootUrl);
				}
			}, CardUnit.urls);
		} finally {
			facard.shutdown();
		}
	}
}