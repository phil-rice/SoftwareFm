/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.explorer.eclipse;

import java.io.File;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.display.browser.BrowserFeedConfigurator;
import org.softwareFm.display.browser.RssFeedConfigurator;
import org.softwareFm.display.browser.SnippetFeedConfigurator;
import org.softwareFm.display.browser.TweetFeedConfigurator;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwarefm.collections.explorer.Explorer;
import org.softwarefm.collections.explorer.IMasterDetailSocial;

public class AbstractExplorerView extends ViewPart {

	final protected String rootUrl = "/softwareFm/data";

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();
		final CardConfig cardConfig = makeCardConfig(parent);
		IMasterDetailSocial masterDetailSocial = IMasterDetailSocial.Utils.masterDetailSocial(parent);
		Size.resizeMeToParentsSize(masterDetailSocial.getControl());

		IPlayListGetter playListGetter = new ArtifactPlayListGetter(cardConfig.cardDataStore);
		final Explorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, activator.getServiceExecutor(), playListGetter);
		new BrowserFeedConfigurator().configure(explorer);
		new RssFeedConfigurator().configure(explorer);
		new TweetFeedConfigurator().configure(explorer);
		new SnippetFeedConfigurator().configure(explorer);

		final IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
		String welcomeUrl = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.webPageWelcomeUrl);

		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it

		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(final BindingRipperResult ripperResult) {
				final String hexDigest = ripperResult.hexDigest;
				IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
				String jarUrl = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));
				// System.out.println("Digest: " + hexDigest + "\n Url: " + jarUrl);
				cardConfig.cardDataStore.processDataFor(jarUrl, new ICardDataStoreCallback<Void>() {
					@Override
					public Void process(String jarUrl, Map<String, Object> result) throws Exception {
						AbstractExplorerView.this.process(cardConfig, explorer, ripperResult, result);
						return null;
					}

					@Override
					public Void noData(String url) throws Exception {
						if (ripperResult != null && ripperResult.path != null)
							processNoData(cardConfig, explorer, resourceGetter, ripperResult);
						return null;
					}

				});
			}
		});
		masterDetailSocial.hideMaster();
		explorer.processUrl(DisplayConstants.browserFeedType, welcomeUrl);

	}

	protected CardConfig makeCardConfig(Composite parent) {
		return Activator.getDefault().getCardConfig(parent);
	}

	protected void process(final CardConfig cardConfig, final Explorer explorer, final BindingRipperResult ripperResult, Map<String, Object> result) {
		String artifactUrl = makeUrl(ripperResult, cardConfig, result);
		explorer.displayCard(artifactUrl, new CardAndCollectionDataStoreAdapter());
		showRadioChannelFor(cardConfig, explorer, artifactUrl);
	}

	protected void showRadioChannelFor(CardConfig cardConfig, final Explorer explorer, String artifactUrl) {
		explorer.selectAndNext(artifactUrl);
	}

	protected void processNoData(CardConfig cardConfig, final Explorer explorer, final IResourceGetter resourceGetter, final BindingRipperResult ripperResult) {
		final String hexDigest = ripperResult.hexDigest;
		final String unknownJarUrl = IResourceGetter.Utils.getOrException(resourceGetter, CardConstants.webPageUnknownJarUrl);
		File file = ripperResult.path.toFile();
		if (Files.extension(file.toString()).equals("jar")) {
			explorer.displayUnrecognisedJar(file, hexDigest);
			explorer.processUrl(DisplayConstants.browserFeedType, unknownJarUrl);
		}
	}

	protected String makeUrl(BindingRipperResult ripperResult, final CardConfig cardConfig, Map<String, Object> result) {
		IUrlGenerator cardUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
		String artifactUrl = cardUrlGenerator.findUrlFor(result);
		return artifactUrl;
	}

	@Override
	public void setFocus() {
	}

}