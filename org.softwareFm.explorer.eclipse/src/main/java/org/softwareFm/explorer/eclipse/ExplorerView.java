package org.softwareFm.explorer.eclipse;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.browser.TweetFeedConfigurator;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.collections.ICollectionConfigurationFactory;
import org.softwarefm.collections.explorer.Explorer;
import org.softwarefm.collections.explorer.MasterDetailSocial;

public class ExplorerView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();

		final String rootUrl = "/softwareFm/data";
		IRepositoryFacard repository = activator.getRepository();
		final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, repository)));
		MasterDetailSocial masterDetailSocial = new MasterDetailSocial(parent, SWT.NULL);
		Size.resizeMeToParentsSize(masterDetailSocial.getControl());

		IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardConfig.cardDataStore);
		final Explorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, activator.getServiceExecutor(), playListGetter);
		new BrowserFeedConfigurator().configure(explorer);
		new RssFeedConfigurator().configure(explorer);
		new TweetFeedConfigurator().configure(explorer);

		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it

		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(final BindingRipperResult ripperResult) {
				final String hexDigest = ripperResult.hexDigest;
				IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
				String jarUrl = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));
				System.out.println("Digest: " + hexDigest + "\n Url: " + jarUrl);
				cardConfig.cardDataStore.processDataFor(jarUrl, new ICardDataStoreCallback<Void>() {
					@Override
					public Void process(String jarUrl, Map<String, Object> result) throws Exception {
						IUrlGenerator cardUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
						String artifactUrl = cardUrlGenerator.findUrlFor(result);
						explorer.displayCard(artifactUrl, new CardAndCollectionDataStoreAdapter());
						explorer.selectAndNext(artifactUrl);
						return null;
					}

					@Override
					public Void noData(String url) throws Exception {
						File file = ripperResult.path.toFile();
						if (Files.extension(file.toString()).equals("jar")) {
							explorer.displayUnrecognisedJar(file, hexDigest);
							explorer.processUrl(DisplayConstants.browserFeedType, "www.softwarefm.com");
						}
						return null;
					}
				});
			}
		});
		explorer.processUrl(DisplayConstants.browserFeedType, "www.softwarefm.com");

	}

	@Override
	public void setFocus() {
	}

}
