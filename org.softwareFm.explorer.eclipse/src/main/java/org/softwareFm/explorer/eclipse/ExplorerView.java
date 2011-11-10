package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.eclipse.ISelectedBindingListener;
import org.softwareFm.eclipse.ISelectedBindingManager;
import org.softwareFm.jdtBinding.api.BindingRipperResult;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.maps.Maps;

public class ExplorerView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		final Activator activator = Activator.getDefault();

		final String rootUrl = "/softwareFm/data";
		IRepositoryFacard repository = activator.getRepository();
		final CardConfig cardConfig = new BasicCardConfigurator().configure(parent.getDisplay(), new CardConfig(ICardFactory.Utils.cardFactory(), ICardDataStore.Utils.repositoryCardDataStore(parent, repository)));
		MasterDetailSocial masterDetailSocial = new MasterDetailSocial(parent, SWT.NULL);
		Size.resizeMeToParentsSize(masterDetailSocial.getControl());

		final Explorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, activator.getServiceExecutor());
		ISelectedBindingManager selectedBindingManager = activator.getSelectedBindingManager();// creates it

		selectedBindingManager.addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
			@Override
			public void selectionOccured(final BindingRipperResult ripperResult) {
				final String hexDigest = ripperResult.hexDigest;
				IUrlGenerator jarUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarUrlKey);
				String url = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JdtConstants.hexDigestKey, hexDigest));
				System.out.println("Digest: " + hexDigest + "\n Url: " + url);
				cardConfig.cardDataStore.processDataFor(url, new ICardDataStoreCallback<Void>() {
					@Override
					public Void process(String url, Map<String, Object> result) throws Exception {
						IUrlGenerator cardUrlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.artifactUrlKey);
						String cardUrl = cardUrlGenerator.findUrlFor(result);
						explorer.displayCard(cardUrl);
						return null;
					}

					@Override
					public Void noData(String url) throws Exception {
						explorer.displayUnrecognisedJar( ripperResult.path.toFile(), hexDigest);
						return null;
					}
				});
			}
		});

	}

	@Override
	public void setFocus() {
	}

}
