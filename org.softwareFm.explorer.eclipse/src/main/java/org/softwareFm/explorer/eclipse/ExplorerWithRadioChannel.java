package org.softwareFm.explorer.eclipse;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Button;
import org.softwareFm.display.swt.Swts.Grid;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwarefm.collections.ICollectionConfigurationFactory;

public class ExplorerWithRadioChannel {
	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		try {
			final String rootUrl = "/softwareFm/data";
			final String firstUrl = "/softwareFm/data/org";

			Show.display(ExplorerWithRadioChannel.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					Composite explorerAndButton = Swts.newComposite(from, SWT.NULL, "ExplorerAndButton");
					explorerAndButton.setLayout(new GridLayout());
					Composite buttonPanel = Swts.newComposite(explorerAndButton, SWT.NULL, "ButtonPanel");
					buttonPanel.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());

					final ICardDataStore cardDataStore = ICardDataStore.Utils.repositoryCardDataStore(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(explorerAndButton, SWT.NULL);
					final IExplorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, service, new ArtifactPlayListGetter(cardDataStore));

					new BrowserFeedConfigurator().configure( explorer);
					new RssFeedConfigurator().configure( explorer);

					explorer.displayCard(firstUrl);
					buttonPanel.setLayoutData(Grid.makeGrabHorizonalAndFillGridData());
					masterDetailSocial.getComposite().setLayoutData(Grid.makeGrabHorizonalVerticalAndFillGridData());

					Button.makePushButton(buttonPanel, null, "Next", false, new Runnable() {
						@Override
						public void run() {
							explorer.next();
						}
					});
					Button.makePushButton(buttonPanel, null, "Prev", false, new Runnable() {
						@Override
						public void run() {
							explorer.previous();
						}
					});
					final AtomicReference<String> url = new AtomicReference<String>();
					explorer.addCardListener(new ICardChangedListener() {

						@Override
						public void valueChanged(ICard card, String key, Object newValue) {

						}

						@Override
						public void cardChanged(ICardHolder cardHolder, ICard card) {
							url.set(card.url());
						}
					});
					Button.makePushButton(buttonPanel, null, "Select Artifact", false, new Runnable() {
						@Override
						public void run() {
							explorer.selectAndNext(url.get());
						}
					});
					Button.makePushButton(buttonPanel, null, "Show List", false, new Runnable() {
						@Override
						public void run() {
							explorer.showContents();
						}
					});
					Swts.Row.setRowDataFor(100, 20, buttonPanel.getChildren());

					return explorerAndButton;
				}
			});
		} finally {
			facard.shutdown();
			service.shutdown();
		}

	}
}
