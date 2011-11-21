package org.softwareFm.card.card.internal;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.internal.CardUnit;
import org.softwareFm.card.card.internal.SingleCardExplorer;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.internal.CardDataStoreForRepository;
import org.softwareFm.card.softwareFm.internal.SoftwareFmCardConfigurator;
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
					final CardConfig cardConfig = new SoftwareFmCardConfigurator().configure(parent.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					return new SingleCardExplorer(parent, cardConfig, rootUrl);
				}
			}, CardUnit.urls);
		} finally {
			facard.shutdown();
		}
	}
}
