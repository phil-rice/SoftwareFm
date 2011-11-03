package org.softwareFm.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.strings.Strings;

public class SingleCardExplorerUnit {

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/data";
		try {
			Swts.xUnit(SingleCardExplorerUnit.class.getSimpleName(), new ISituationListAndBuilder<SingleCardExplorer>() {
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
